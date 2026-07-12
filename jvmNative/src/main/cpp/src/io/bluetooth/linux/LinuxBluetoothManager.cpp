#include "LinuxBluetoothManager.hpp"
#include "LinuxRfcommConnector.hpp"
#include "LinuxStdBusProperties.hpp"

#include <boost/log/trivial.hpp>
#include <sdbus-c++/sdbus-c++.h>

#include <algorithm>
#include <functional>
#include <map>
#include <mutex>
#include <vector>

namespace clipbird::io::bluetooth {

LinuxBluetoothManager::LinuxBluetoothManager()
  : objectManager(sdbus::createProxy(*connection, sdbus::ServiceName("org.bluez"), sdbus::ObjectPath("/"))) {

  objectManager
    ->uponSignal("InterfacesAdded")
    .onInterface(kObjectManagerInterface)
    .call([this](const sdbus::ObjectPath&, const std::map<sdbus::InterfaceName, std::map<sdbus::PropertyName, sdbus::Variant>>& interfacesAndProperties) {
      if (interfacesAndProperties.find(sdbus::InterfaceName(kBluezDeviceInterface)) != interfacesAndProperties.end()) {
        this->notifyBondedDevicesChanged();
      }
    });

  objectManager
    ->uponSignal("InterfacesRemoved")
    .onInterface(kObjectManagerInterface)
    .call([this](const sdbus::ObjectPath&, const std::vector<sdbus::InterfaceName>& interfaces) {
      if (std::find(interfaces.begin(), interfaces.end(), sdbus::InterfaceName(kBluezDeviceInterface)) != interfaces.end()) {
        this->notifyBondedDevicesChanged();
      }
    });

  connection->enterEventLoopAsync();
}

LinuxBluetoothManager::~LinuxBluetoothManager() {
  this->connection->leaveEventLoop();
  this->removeBondedDevicesChangedCallback();
}

void LinuxBluetoothManager::setBondedDevicesChangedCallback(std::function<void()> callback) {
  std::lock_guard<std::mutex> guard(bondedDevicesChangedMutex);
  bondedDevicesChangedCallback = std::move(callback);
}

void LinuxBluetoothManager::removeBondedDevicesChangedCallback() {
  std::unique_lock<std::mutex> lock(bondedDevicesChangedMutex);
  bondedDevicesChangedCallback = {};
  bondedDevicesChangedCondition.wait(lock, [this]() {
    return bondedDevicesChangedInFlightCount == 0;
  });
}

std::vector<bluetooth::BluetoothDevice> LinuxBluetoothManager::bondedDevices() {
  using InterfaceProperties = std::map<sdbus::InterfaceName, LinuxStdBusProperties>;
  using ManagedObjects = std::map<sdbus::ObjectPath, InterfaceProperties>;

  ManagedObjects managedObjects;

  objectManager->callMethod("GetManagedObjects")
    .onInterface(kObjectManagerInterface)
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

    devices.push_back(bluetooth::BluetoothDevice{ *address, name.value_or("Unknown") });
  }

  return devices;
}

std::string LinuxBluetoothManager::localName() {
  using InterfaceProperties = std::map<sdbus::InterfaceName, LinuxStdBusProperties>;
  using ManagedObjects = std::map<sdbus::ObjectPath, InterfaceProperties>;

  ManagedObjects managedObjects;

  objectManager->callMethod("GetManagedObjects")
    .onInterface(kObjectManagerInterface)
    .storeResultsTo(managedObjects);

  for (const auto& [path, interfaces] : managedObjects) {
    auto adapterInterface = interfaces.find(sdbus::InterfaceName(kBluezAdapterInterface));

    if (adapterInterface == interfaces.end()) {
      continue;
    }

    const auto& properties = adapterInterface->second;

    auto name = getProperty<std::string>(properties, "Alias");

    if (!name.has_value()) {
      name = getProperty<std::string>(properties, "Name");
    }

    if (name.has_value()) {
      return *name;
    }
  }

  throw std::runtime_error("No Bluetooth adapter found");
}

void LinuxBluetoothManager::notifyBondedDevicesChanged() {
  std::function<void()> callback;

  {
    std::lock_guard<std::mutex> guard(bondedDevicesChangedMutex);

    if (!bondedDevicesChangedCallback) {
      return;
    }

    ++bondedDevicesChangedInFlightCount;
    callback = bondedDevicesChangedCallback;
  }

  try {
    callback();
  } catch (const std::exception& e) {
    BOOST_LOG_TRIVIAL(warning) << "Bonded-device change callback failed: " << e.what();
  } catch (...) {
    BOOST_LOG_TRIVIAL(warning) << "Bonded-device change callback failed with an unknown error";
  }

  {
    std::lock_guard<std::mutex> guard(bondedDevicesChangedMutex);

    if (--bondedDevicesChangedInFlightCount == 0) {
      bondedDevicesChangedCondition.notify_all();
    }
  }
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
