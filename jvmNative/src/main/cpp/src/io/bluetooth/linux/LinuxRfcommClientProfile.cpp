#include "LinuxRfcommClientProfile.hpp"

#include <boost/log/trivial.hpp>
#include <boost/uuid/uuid_io.hpp>

#include <exception>
#include <format>
#include <map>
#include <stdexcept>
#include <utility>

namespace clipbird::bluetooth {

LinuxRfcommClientProfile::LinuxRfcommClientProfile(sdbus::IConnection& connection, const boost::uuids::uuid& serviceUuid)
  : connection(connection),
    serviceUuid(serviceUuid),
    connectionFuture(connectionPromise.get_future()),
    objectPath(nextProfileObjectPath()),
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
    {"Name", sdbus::Variant(std::string("Clipbird RFCOMM Client"))},
    {"Role", sdbus::Variant(std::string("client"))},
    {"RequireAuthentication", sdbus::Variant(false)},
    {"RequireAuthorization", sdbus::Variant(false)}
  };

  profileManager->callMethod("RegisterProfile")
    .onInterface("org.bluez.ProfileManager1")
    .withArguments(objectPath, boost::uuids::to_string(serviceUuid), options);

  registered = true;
}

sdbus::UnixFd LinuxRfcommClientProfile::connect(const sdbus::ObjectPath& devicePath) {
  auto device = sdbus::createProxy(connection, sdbus::ServiceName("org.bluez"), devicePath);
  device->callMethod("ConnectProfile").onInterface("org.bluez.Device1").withArguments(boost::uuids::to_string(serviceUuid));
  return connectionFuture.get();
}

std::string LinuxRfcommClientProfile::nextProfileObjectPath() {
  return std::format("/com/srilakshmikanthanp/clipbird/rfcomm/client/profile/{}", nextId.fetch_add(1));
}

void LinuxRfcommClientProfile::onNewConnection(const sdbus::ObjectPath&, sdbus::UnixFd fd, const std::map<std::string, sdbus::Variant>&) {
  try {
    connectionPromise.set_value(std::move(fd));
  } catch (const std::future_error& e) {
    BOOST_LOG_TRIVIAL(debug) << "Ignored duplicate RFCOMM client connection: " << e.what();
  }
}

void LinuxRfcommClientProfile::release() {
  registered = false;

  try {
    connectionPromise.set_exception(std::make_exception_ptr(std::runtime_error("BlueZ RFCOMM profile was released before a connection was provided")));
  } catch (const std::future_error& e) {
    BOOST_LOG_TRIVIAL(debug) << "Ignored duplicate RFCOMM client profile release: " << e.what();
  }
}

LinuxRfcommClientProfile::~LinuxRfcommClientProfile() {
  if (registered) {
    this->release();
  } else {
    BOOST_LOG_TRIVIAL(debug) << "BlueZ RFCOMM client profile was already released";
    return;
  }

  try {
    profileManager->callMethod("UnregisterProfile").onInterface("org.bluez.ProfileManager1").withArguments(objectPath);
  } catch (const sdbus::Error& e) {
    BOOST_LOG_TRIVIAL(warning) << "Failed to unregister BlueZ RFCOMM client profile: " << e.what();
  }
}

}  // namespace clipbird::bluetooth
