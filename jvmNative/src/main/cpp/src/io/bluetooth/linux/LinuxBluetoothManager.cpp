#include "LinuxBluetoothManager.hpp"
#include "LinuxRfcommConnector.hpp"
#include "LinuxStdBusProperties.hpp"

#include <sdbus-c++/sdbus-c++.h>

#include <map>
#include <vector>

namespace clipbird::io::bluetooth {

std::vector<bluetooth::BluetoothDevice> LinuxBluetoothManager::bondedDevices() {
  using InterfaceProperties = std::map<sdbus::InterfaceName, LinuxStdBusProperties>;
  using ManagedObjects = std::map<sdbus::ObjectPath, InterfaceProperties>;

  auto manager = sdbus::createProxy(*connection, sdbus::ServiceName("org.bluez"), sdbus::ObjectPath("/"));

  ManagedObjects managedObjects;

  manager->callMethod("GetManagedObjects")
    .onInterface("org.freedesktop.DBus.ObjectManager")
    .storeResultsTo(managedObjects);

  std::vector<bluetooth::BluetoothDevice> devices;

  for (const auto& [path, interfaces] : managedObjects) {
    auto deviceInterface = interfaces.find(sdbus::InterfaceName("org.bluez.Device1"));

    if (deviceInterface == interfaces.end()) {
      continue;
    }

    const auto& properties = deviceInterface->second;

    if (!getProperty<bool>(properties, "Paired").value_or(false)) {
      continue;
    }

    auto address = getProperty<std::string>(properties, "Address");

    if (!address.has_value()) {
      continue;
    }

    auto name = getProperty<std::string>(properties, "Alias");

    if (!name.has_value()) {
      name = getProperty<std::string>(properties, "Name");
    }

    auto uuids = getProperty<std::vector<boost::uuids::uuid>>(properties, "UUIDs");

    devices.push_back(bluetooth::BluetoothDevice{
      *address,
      name.value_or("Unknown"),
      uuids.value_or(std::vector<boost::uuids::uuid>{})
    });
  }

  return devices;
}

std::unique_ptr<io::Channel> LinuxBluetoothManager::connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) {
  LinuxRfcommConnector connector(address, serviceUuid);
  auto fd = connector.getFd();
  return std::make_unique<LinuxRfcommChannel>(fd.release());
}

std::unique_ptr<io::Server> LinuxBluetoothManager::startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) {
  return std::make_unique<LinuxRfcommServer>(serviceName, serviceUuid);
}

}  // namespace clipbird::io::bluetooth
