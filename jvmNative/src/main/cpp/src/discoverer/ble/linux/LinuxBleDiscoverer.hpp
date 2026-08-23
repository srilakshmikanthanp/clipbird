#pragma once

#include <atomic>
#include <cstdint>
#include <map>
#include <memory>
#include <mutex>
#include <vector>

#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

#include "discoverer/Discoverer.hpp"
#include "discoverer/DiscovererListener.hpp"

namespace clipbird::discoverer::ble {

class LinuxBleDiscoverer final : public Discoverer {
 public:
  LinuxBleDiscoverer(const boost::uuids::uuid& serviceUuid, DiscovererListener& listener);
  ~LinuxBleDiscoverer() override;

  void startDiscovery() override;
  void stopDiscovery() override;

 private:
  std::optional<std::int64_t> extractDeviceId(const std::map<std::string, sdbus::Variant>& properties);
  sdbus::ObjectPath getAdapterPath();

 private:
  void onAdapterPropertiesChanged(
    const std::string& ifaceName,
    const std::map<std::string, sdbus::Variant>& changedProps,
    const std::vector<std::string>& invalidated
  );

  void onInterfacesAdded(
    const sdbus::ObjectPath& path,
    const std::map<std::string, std::map<std::string, sdbus::Variant>>& interfaces
  );

  void onDevicePropertiesChanged(
    sdbus::Message message
  );

 private:
  boost::uuids::uuid serviceUuid;
  std::unique_ptr<sdbus::IConnection> connection;
  std::unique_ptr<sdbus::IProxy> bluezProxy;
  std::unique_ptr<sdbus::IProxy> adapterProxy;
  sdbus::Slot deviceMatchSlot;
  std::atomic<bool> discovering{false};

 private:
  inline static constexpr const char* kObjectManagerInterface = "org.freedesktop.DBus.ObjectManager";
  inline static constexpr const char* kPropertiesInterface = "org.freedesktop.DBus.Properties";
  inline static constexpr const char* kBluezService = "org.bluez";
  inline static constexpr const char* kRootPath = "/";
  inline static constexpr const char* kAdapterInterface = "org.bluez.Adapter1";
  inline static constexpr const char* kDeviceInterface = "org.bluez.Device1";
  inline static constexpr std::uint16_t kCompanyId = 0xFFFF;
  inline static constexpr std::size_t kPayloadSize = 24;
  inline static constexpr std::size_t kUuidSize = 16;
};

}  // namespace clipbird::discoverer::ble
