#include "LinuxBleAdvertiser.hpp"

#include <map>
#include <stdexcept>
#include <string>

#include "advertiser/ble/ble_advertiser_code.h"
#include "utility/utility.hpp"

namespace clipbird::advertiser::ble {

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

void LinuxBleAdvertiser::onReleaseAdvertisement() {
  if (advertising.exchange(false)) {
    listener.onAdvertisingStopped();
  }
}

void LinuxBleAdvertiser::onAdapterPropertiesChanged(
  const std::string& ifaceName,
  const std::map<std::string, sdbus::Variant>& changedProps,
  const std::vector<std::string>& invalidated
) {
  if (ifaceName == kAdapterInterface) {
    auto it = changedProps.find("Powered");
    if (it != changedProps.end() && !it->second.get<bool>()) {
      if (advertising.exchange(false)) {
        listener.onAdvertisingStopped();
      }
    }
  }
}

LinuxBleAdvertiser::LinuxBleAdvertiser(
  const boost::uuids::uuid& serviceUuid,
  const std::vector<std::uint8_t>& serviceData,
  AdvertiserListener& events
):  Advertiser(events),
    connection(sdbus::createSystemBusConnection()),
    bluezProxy(sdbus::createProxy(*connection, sdbus::ServiceName(kBluezService), sdbus::ObjectPath(kRootPath))),
    data(std::make_unique<LinuxBleAdvertisementData>(
      *connection,
      sdbus::ObjectPath(kAdvertisementPath),
      serviceUuid,
      serviceData,
      std::bind(&LinuxBleAdvertiser::onReleaseAdvertisement, this)
    )) {
  connection->enterEventLoopAsync();
}

void LinuxBleAdvertiser::startAdvertising() {
  if (advertising.exchange(true)) {
    listener.onAdvertisingFailed(clipbird_advertiser_ble_error_code::CLIPBIRD_ADVERTISER_BLE_ALREADY_ADVERTISING, "Already advertising");
    return;
  }

  sdbus::ObjectPath adapterPath;

  try {
    adapterPath = getAdvertisingManagerAdapterPath();
  } catch (const std::exception& e) {
    advertising = false;
    listener.onAdvertisingFailed(clipbird_advertiser_ble_error_code::CLIPBIRD_ADVERTISER_BLE_ADAPTER_NOT_FOUND, e.what());
    return;
  }

  advertisingManagerProxy = sdbus::createProxy(
    *connection,
    sdbus::ServiceName(kBluezService),
    adapterPath
  );

  advertisingManagerProxy->uponSignal("PropertiesChanged")
    .onInterface("org.freedesktop.DBus.Properties")
    .call([this](const std::string& ifaceName, const std::map<std::string, sdbus::Variant>& changedProps, const std::vector<std::string>& invalidated) {
      onAdapterPropertiesChanged(ifaceName, changedProps, invalidated);
    });

  auto registerCallback = [this](std::optional<sdbus::Error> error) {
    if (error) {
      advertising = false;
      listener.onAdvertisingFailed(clipbird_advertiser_ble_error_code::CLIPBIRD_ADVERTISER_BLE_REGISTRATION_FAILED, error->getMessage());
    } else {
      advertising = true;
      listener.onAdvertisingStarted();
    }
  };

  advertisingManagerProxy->callMethodAsync("RegisterAdvertisement")
    .onInterface(kAdvertisingManagerInterface)
    .withArguments(data->getObjectPath(), std::map<std::string, sdbus::Variant>{})
    .uponReplyInvoke(registerCallback);
}

void LinuxBleAdvertiser::stopAdvertising() {
  if (!advertising.exchange(false)) return;

  if (!advertisingManagerProxy) return;

  advertisingManagerProxy->callMethodAsync("UnregisterAdvertisement")
    .onInterface(kAdvertisingManagerInterface)
    .withArguments(data->getObjectPath())
    .getResultAsFuture()
    .get();

  listener.onAdvertisingStopped();
}

LinuxBleAdvertiser::~LinuxBleAdvertiser() {
  utility::logOnThrow("Failed to stop BLE advertising", [this] { this->stopAdvertising(); });
  utility::logOnThrow("Failed to leave BLE advertiser event loop", [this] { connection->leaveEventLoop(); });
}

}  // namespace clipbird::advertiser::ble
