#include "LinuxBleAdvertiser.hpp"

#include <future>
#include <map>
#include <optional>
#include <stdexcept>
#include <string>
#include <utility>

namespace clipbird::advertiser::ble {
LinuxBleAdvertiser::LinuxBleAdvertiser(const boost::uuids::uuid& serviceUuid, const std::vector<std::uint8_t>& serviceData)
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
  connection->enterEventLoopAsync();
}

LinuxBleAdvertiser::~LinuxBleAdvertiser() {
  this->stopAdvertising();
  connection->leaveEventLoop();
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
  if (this->isAdvertising()) {
    throw std::runtime_error("Already advertising");
  }

  const auto adapterPath = getAdvertisingManagerAdapterPath();

  auto advertisingManagerProxy = sdbus::createProxy(
    *connection,
    sdbus::ServiceName(kBluezService),
    adapterPath
  );

  auto options = std::map<std::string, sdbus::Variant>{};

  advertisingManagerProxy->callMethodAsync("RegisterAdvertisement")
    .onInterface(kAdvertisingManagerInterface)
    .withArguments(advertisementData->getObjectPath(), options)
    .getResultAsFuture()
    .get();

  this->advertising = true;
}

bool LinuxBleAdvertiser::isAdvertising() const {
  return advertising;
}

void LinuxBleAdvertiser::stopAdvertising() {
  if (!this->isAdvertising()) {
    return;
  }

  const auto adapterPath = getAdvertisingManagerAdapterPath();

  auto advertisingManagerProxy = sdbus::createProxy(
    *connection,
    sdbus::ServiceName(kBluezService),
    adapterPath
  );

  advertisingManagerProxy->callMethodAsync("UnregisterAdvertisement")
    .onInterface(kAdvertisingManagerInterface)
    .withArguments(advertisementData->getObjectPath())
    .getResultAsFuture()
    .get();

  this->advertising = false;
}
}  // namespace clipbird
