#include "LinuxRfcommServerProfile.hpp"

#include <bluetooth/bluetooth.h>
#include <boost/log/trivial.hpp>
#include <boost/uuid/uuid_io.hpp>
#include <cstring>
#include <fcntl.h>
#include <map>
#include <stdexcept>
#include <sys/socket.h>

#include "LinuxRfcommChannel.hpp"
#include "io/IOException.hpp"

namespace clipbird::io::bluetooth {

LinuxRfcommServerProfile::LinuxRfcommServerProfile(
  sdbus::IConnection& connection,
  const std::string& serviceName,
  const boost::uuids::uuid& serviceUuid
): connection(connection),
   objectPath("/com/srilakshmikanthanp/clipbird/rfcomm/server/profile"),
   object(sdbus::createObject(connection, objectPath)),
   profileManager(sdbus::createProxy(connection, sdbus::ServiceName("org.bluez"), sdbus::ObjectPath("/org/bluez"))) {

  object->addVTable(
    sdbus::registerMethod("RequestDisconnection").withInputParamNames("device").implementedAs([](const sdbus::ObjectPath&) {}),
    sdbus::registerMethod("NewConnection").withInputParamNames("device", "fd", "properties").implementedAs([this](
      const sdbus::ObjectPath& device, sdbus::UnixFd fd, const std::map<std::string, sdbus::Variant>& properties
    ) {
      this->onNewConnection(device, std::move(fd), properties);
    }),
    sdbus::registerMethod("Cancel").implementedAs([]() {}),
    sdbus::registerMethod("Release").implementedAs([this]() {
      this->release();
    })
  ).forInterface("org.bluez.Profile1");

  auto options = std::map<std::string, sdbus::Variant>{
    {"Name", sdbus::Variant(serviceName)},
    {"Role", sdbus::Variant(std::string("server"))},
    {"Channel", sdbus::Variant(uint16_t(0))},
    {"RequireAuthentication", sdbus::Variant(true)},
    {"RequireAuthorization", sdbus::Variant(false)}
  };

  profileManager->callMethod("RegisterProfile")
    .onInterface("org.bluez.ProfileManager1")
    .withArguments(objectPath, boost::uuids::to_string(serviceUuid), options);
  registered = true;
}

void LinuxRfcommServerProfile::onNewConnection(const sdbus::ObjectPath& device, sdbus::UnixFd fd, const std::map<std::string, sdbus::Variant>& properties) {
  try {
    BOOST_LOG_TRIVIAL(debug) << "RFCOMM NewConnection from " << std::string(device);
    acceptedConnections.push(std::make_tuple(std::move(fd), std::string(device)));
  } catch (const boost::sync_queue_is_closed& e) {
    BOOST_LOG_TRIVIAL(debug) << "Ignored RFCOMM server connection because the profile is closed: " << e.what();
  }
}

void LinuxRfcommServerProfile::release() {
  registered = false;
  acceptedConnections.close();
}

std::unique_ptr<io::Channel> LinuxRfcommServerProfile::accept() {
  std::tuple<sdbus::UnixFd, std::string> pending;

  try {
    pending = acceptedConnections.pull();
  } catch (const boost::sync_queue_is_closed&) {
    throw io::IOException("BlueZ RFCOMM server has been closed");
  }

  auto& [fd, devicePath] = pending;
  std::string address;

  try {
    auto device = sdbus::createProxy(connection, sdbus::ServiceName("org.bluez"), sdbus::ObjectPath(devicePath));
    address = device->getProperty("Address").onInterface("org.bluez.Device1").get<std::string>();
  } catch (const sdbus::Error& e) {
    throw io::IOException("Failed to get Bluetooth device address for RFCOMM connection: " + std::string(e.what()));
  }

  struct bt_security security = {};
  socklen_t securityLength = sizeof(security);

  if (::getsockopt(fd.get(), SOL_BLUETOOTH, BT_SECURITY, &security, &securityLength) < 0) {
    throw io::IOException("Failed to read RFCOMM link security level: " + std::string(strerror(errno)));
  }

  if (security.level < BT_SECURITY_HIGH) {
    throw io::IOException("Rejected an insufficiently secured RFCOMM connection from " + address);
  }

  int flags = ::fcntl(fd.get(), F_GETFL, 0);

  if (flags == -1 || ::fcntl(fd.get(), F_SETFL, flags & ~O_NONBLOCK) == -1) {
    throw io::IOException("Failed to set RFCOMM socket to blocking mode: " + std::string(strerror(errno)));
  }

  return std::make_unique<LinuxRfcommChannel>(fd.release(), address);
}

LinuxRfcommServerProfile::~LinuxRfcommServerProfile() {
  if (registered) {
    this->release();
  } else {
    BOOST_LOG_TRIVIAL(debug) << "BlueZ RFCOMM server profile was already released";
    return;
  }

  try {
    profileManager->callMethod("UnregisterProfile").onInterface("org.bluez.ProfileManager1").withArguments(objectPath);
  } catch (const sdbus::Error& e) {
    BOOST_LOG_TRIVIAL(warning) << "Failed to unregister BlueZ RFCOMM server profile: " << e.what();
  }
}

}  // namespace clipbird::io::bluetooth
