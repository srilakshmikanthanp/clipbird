#include "LinuxBleDiscoverer.hpp"

#include <cstring>
#include <map>
#include <stdexcept>
#include <string>
#include <vector>

#include "discoverer/ble/ble_discoverer_code.h"
#include "utility/utility.hpp"

namespace clipbird::discoverer::ble {

std::optional<std::int64_t> LinuxBleDiscoverer::extractDeviceId(const std::map<std::string, sdbus::Variant>& properties) {
  auto mfgDataVariant = properties.find("ManufacturerData");
  if (mfgDataVariant == properties.end()) return std::nullopt;
  const auto& mfgDataMap = mfgDataVariant->second.get<std::map<std::uint16_t, sdbus::Variant>>();

  auto dataIt = mfgDataMap.find(kCompanyId);
  if (dataIt == mfgDataMap.end()) return std::nullopt;

  const auto& payload = dataIt->second.get<std::vector<std::uint8_t>>();
  if (payload.size() < kPayloadSize) return std::nullopt;
  if (std::memcmp(payload.data(), serviceUuid.data, kUuidSize) != 0) return std::nullopt;

  std::int64_t id = 0;

  for (std::size_t i = kUuidSize; i < kPayloadSize; ++i) {
    id = (id << 8) | static_cast<std::int64_t>(payload[i] & 0xFF);
  }

  return id;
}

sdbus::ObjectPath LinuxBleDiscoverer::getAdapterPath() {
  using Properties = std::map<sdbus::PropertyName, sdbus::Variant>;
  using Interfaces = std::map<sdbus::InterfaceName, Properties>;
  using Objects = std::map<sdbus::ObjectPath, Interfaces>;

  Objects managedObjects;

  bluezProxy->callMethod("GetManagedObjects")
    .onInterface(kObjectManagerInterface)
    .storeResultsTo(managedObjects);

  for (const auto& [objectPath, interfaces] : managedObjects) {
    if (interfaces.contains(sdbus::InterfaceName(kAdapterInterface))) {
      return objectPath;
    }
  }

  throw std::runtime_error("No Bluetooth adapter found");
}

void LinuxBleDiscoverer::onInterfacesAdded(
  const sdbus::ObjectPath& path,
  const std::map<std::string, std::map<std::string, sdbus::Variant>>& interfaces
) {
  auto interface = interfaces.find(kDeviceInterface);
  if (!discovering || interface == interfaces.end()) return;

  auto deviceId = extractDeviceId(interface->second);
  if (deviceId.has_value()) {
    listener.onDeviceDiscovered(*deviceId);
  }
}

void LinuxBleDiscoverer::onDevicePropertiesChanged(sdbus::Message message) {
  std::string ifaceName;
  std::map<std::string, sdbus::Variant> changedProps;
  std::vector<std::string> invalidated;
  message >> ifaceName >> changedProps >> invalidated;

  auto deviceId = extractDeviceId(changedProps);
  if (deviceId.has_value()) {
    listener.onDeviceDiscovered(*deviceId);
  }
}

void LinuxBleDiscoverer::onAdapterPropertiesChanged(
  const std::string& ifaceName,
  const std::map<std::string, sdbus::Variant>& changedProps,
  const std::vector<std::string>& invalidated
) {
  if (ifaceName != kAdapterInterface) {
    return;
  }

  auto it = changedProps.find("Powered");

  if (it == changedProps.end() || it->second.get<bool>()) {
    return;
  }

  const bool wasDiscovering = discovering.exchange(false);

  stopDiscovery();

  if (wasDiscovering) {
    listener.onDiscoveryStopped();
  }
}

LinuxBleDiscoverer::LinuxBleDiscoverer(
  const boost::uuids::uuid& serviceUuid,
  DiscovererListener& listener
) : Discoverer(listener),
    serviceUuid(serviceUuid),
    connection(sdbus::createSystemBusConnection()),
    bluezProxy(sdbus::createProxy(*connection, sdbus::ServiceName(kBluezService), sdbus::ObjectPath(kRootPath))) {
  bluezProxy->uponSignal("InterfacesAdded")
    .onInterface(kObjectManagerInterface)
    .call([this](const sdbus::ObjectPath& path, const std::map<std::string, std::map<std::string, sdbus::Variant>>& interfaces) {
      onInterfacesAdded(path, interfaces);
    });

  connection->enterEventLoopAsync();
}

void LinuxBleDiscoverer::startDiscovery() {
  if (discovering.exchange(true)) {
    listener.onDiscoveryFailed(CLIPBIRD_DISCOVERER_BLE_ALREADY_DISCOVERING, "Already discovering");
    return;
  }

  sdbus::ObjectPath adapterPath;

  try {
    adapterPath = getAdapterPath();
  } catch (const std::exception& e) {
    discovering = false;
    listener.onDiscoveryFailed(CLIPBIRD_DISCOVERER_BLE_ADAPTER_NOT_FOUND, e.what());
    return;
  }

  adapterProxy = sdbus::createProxy(
    *connection,
    sdbus::ServiceName(kBluezService),
    adapterPath
  );

  adapterProxy->uponSignal("PropertiesChanged")
    .onInterface(kPropertiesInterface)
    .call([this](const std::string& ifaceName, const std::map<std::string, sdbus::Variant>& changedProps, const std::vector<std::string>& invalidated) {
      onAdapterPropertiesChanged(ifaceName, changedProps, invalidated);
    });

  std::map<std::string, sdbus::Variant> filter;

  filter["DuplicateData"] = sdbus::Variant(true);
  filter["Transport"] = sdbus::Variant(std::string("le"));

  try {
    adapterProxy->callMethod("SetDiscoveryFilter")
      .onInterface(kAdapterInterface)
      .withArguments(filter);

    adapterProxy->callMethod("StartDiscovery")
      .onInterface(kAdapterInterface)
      .storeResultsTo();
  } catch (const std::exception& e) {
    discovering = false;
    adapterProxy.reset();
    listener.onDiscoveryFailed(CLIPBIRD_DISCOVERER_BLE_START_FAILED, e.what());
    return;
  }

  deviceMatchSlot = connection->addMatch(
    "type='signal',sender='org.bluez',interface='org.freedesktop.DBus.Properties',member='PropertiesChanged',arg0='org.bluez.Device1'",
    [this](sdbus::Message message) { onDevicePropertiesChanged(std::move(message)); },
    sdbus::return_slot
  );

  listener.onDiscoveryStarted();
}

void LinuxBleDiscoverer::stopDiscovery() {
  const bool wasDiscovering = discovering.exchange(false);

  if (wasDiscovering) {
    adapterProxy->callMethodAsync("StopDiscovery")
      .onInterface(kAdapterInterface)
      .getResultAsFuture()
      .get();
  }

  deviceMatchSlot.reset();

  if (wasDiscovering) {
    listener.onDiscoveryStopped();
  }
}

LinuxBleDiscoverer::~LinuxBleDiscoverer() {
  utility::logOnThrow("Failed to stop BLE discovery", [this] { this->stopDiscovery(); });
  utility::logOnThrow("Failed to leave BLE discoverer event loop", [this] { connection->leaveEventLoop(); });
}

}  // namespace clipbird::discoverer::ble
