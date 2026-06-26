#include "LinuxBleAdvertiser.hpp"

#include <map>
#include <optional>
#include <stdexcept>
#include <string>
#include <utility>

namespace clipbird::bluetooth::ble {
LinuxBleAdvertiser::LinuxBleAdvertiser(const std::string& serviceUuid, const std::vector<std::uint8_t>& serviceData)
  : connection(sdbus::createSystemBusConnection()),
    bluezProxy(sdbus::createProxy(
      *connection,
      sdbus::ServiceName(kBluezService),
      sdbus::ObjectPath(kRootPath)
    )),
    advertisementData(std::make_unique<LinuxBleAdvertisementData>(
      *connection,
      sdbus::ObjectPath(kAdvertisementPath),
      serviceUuid,
      serviceData,
      [this]() { this->advertising = false; }
    )) {
}

sdbus::ObjectPath LinuxBleAdvertiser::getAdvertisingManagerAdapterPath() {
  using Properties = std::map<sdbus::PropertyName, sdbus::Variant>;
  using Interfaces = std::map<sdbus::InterfaceName, Properties>;
  using Objects = std::map<sdbus::ObjectPath, Interfaces>;

  Objects managedObjects;

  bluezProxy->callMethod("GetManagedObjects")
    .onInterface(kObjectManagerInterface)
    .storeResultsTo(managedObjects);

  for (const auto& [objectPath, interfaces] : managedObjects) {
    if (interfaces.contains(sdbus::InterfaceName(kAdapterInterface)) &&
        interfaces.contains(sdbus::InterfaceName(kAdvertisingManagerInterface))) {
      return objectPath;
    }
  }

  throw std::runtime_error("No Bluetooth adapter with LEAdvertisingManager1 interface found");
}

void LinuxBleAdvertiser::startAdvertising() {
  const auto adapterPath = getAdvertisingManagerAdapterPath();

  if (this->isAdvertising()) {
    throw std::runtime_error("Already advertising");
  }

  auto advertisingManagerProxy = sdbus::createProxy(
    *connection,
    sdbus::ServiceName(kBluezService),
    adapterPath
  );

  auto options = std::map<std::string, sdbus::Variant>{};

  try {
    advertisingManagerProxy->callMethod("RegisterAdvertisement")
      .onInterface(kAdvertisingManagerInterface)
      .withArguments(advertisementData->getObjectPath(), options);
    this->advertising = true;
  } catch (const sdbus::Error& e) {
    throw std::runtime_error(std::string("Failed to register advertisement: ") + e.what());
  }
}

bool LinuxBleAdvertiser::isAdvertising() const {
  return advertising;
}

void LinuxBleAdvertiser::stopAdvertising() {
  const auto adapterPath = getAdvertisingManagerAdapterPath();

  if (!this->isAdvertising()) {
    throw std::runtime_error("Not currently advertising");
  }

  auto advertisingManagerProxy = sdbus::createProxy(
    *connection,
    sdbus::ServiceName(kBluezService),
    adapterPath
  );

  try {
    advertisingManagerProxy->callMethod("UnregisterAdvertisement")
      .onInterface(kAdvertisingManagerInterface)
      .withArguments(advertisementData->getObjectPath());
  } catch (const sdbus::Error& e) {
    throw std::runtime_error(std::string("Failed to unregister advertisement: ") + e.what());
  }
}
}  // namespace clipbird
