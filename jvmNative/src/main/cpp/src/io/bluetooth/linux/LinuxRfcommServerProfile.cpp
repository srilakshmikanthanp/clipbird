#include "LinuxRfcommServerProfile.hpp"

#include <boost/log/trivial.hpp>
#include <boost/uuid/uuid_io.hpp>
#include <map>
#include <stdexcept>

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
    {"RequireAuthentication", sdbus::Variant(false)},
    {"RequireAuthorization", sdbus::Variant(false)}
  };

  profileManager->callMethod("RegisterProfile")
    .onInterface("org.bluez.ProfileManager1")
    .withArguments(objectPath, boost::uuids::to_string(serviceUuid), options);
  registered = true;
}

void LinuxRfcommServerProfile::onNewConnection(const sdbus::ObjectPath& device, sdbus::UnixFd fd, const std::map<std::string, sdbus::Variant>& properties) {
  try {
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
