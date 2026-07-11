#include "io/bluetooth/BluetoothDeviceNotFoundException.hpp"
#include "LinuxRfcommConnector.hpp"
#include "LinuxStdBusProperties.hpp"

#include <bluetooth/bluetooth.h>

#include <map>
#include <stdexcept>
#include <string>

namespace clipbird::io::bluetooth {

sdbus::ObjectPath LinuxRfcommConnector::findDevicePath() {
  using InterfaceProperties = std::map<sdbus::InterfaceName, LinuxStdBusProperties>;
  using ManagedObjects = std::map<sdbus::ObjectPath, InterfaceProperties>;

  auto objectManager = sdbus::createProxy(*connection, sdbus::ServiceName("org.bluez"), sdbus::ObjectPath("/"));

  ManagedObjects managedObjects;

  objectManager->callMethod("GetManagedObjects")
    .onInterface("org.freedesktop.DBus.ObjectManager")
    .storeResultsTo(managedObjects);

  for (const auto& [path, interfaces] : managedObjects) {
    auto deviceInterface = interfaces.find(sdbus::InterfaceName("org.bluez.Device1"));

    if (deviceInterface == interfaces.end()) {
      continue;
    }

    auto deviceAddress = getProperty<std::string>(deviceInterface->second, "Address");

    if (!getProperty<bool>(deviceInterface->second, "Paired").value_or(false)) {
      continue;
    }

    if (deviceAddress.has_value() && *deviceAddress == address) {
      return path;
    }
  }

  throw BluetoothDeviceNotFoundException("Bluetooth device not found in BlueZ: " + address);
}

std::string LinuxRfcommConnector::normalize(const std::string& address) {
  std::string normalizedAddress;
  bdaddr_t parsedAddress{};

  if (str2ba(address.c_str(), &parsedAddress) != 0) {
    throw std::invalid_argument("Invalid Bluetooth address: " + address);
  }

  normalizedAddress.resize(18);
  ba2str(&parsedAddress, normalizedAddress.data());
  return normalizedAddress;
}

LinuxRfcommConnector::LinuxRfcommConnector(
  const std::string& address,
  const boost::uuids::uuid& serviceUuid
) : connection(sdbus::createSystemBusConnection()),
    address(normalize(address)),
    serviceUuid(serviceUuid),
    profile(*connection, serviceUuid) {
  connection->enterEventLoopAsync();
}

sdbus::UnixFd LinuxRfcommConnector::getFd() {
  if (connected.exchange(true)) {
    throw std::logic_error("Already connected to RFCOMM service");
  } else {
    return profile.connect(findDevicePath());
  }
}

LinuxRfcommConnector::~LinuxRfcommConnector() {
  connection->leaveEventLoop();
}

}  // namespace clipbird::io::bluetooth
