#pragma once

#include <cstdint>
#include <memory>
#include <string>
#include <vector>

#include <sdbus-c++/sdbus-c++.h>

#include "LinuxBleAdvertisementData.hpp"
#include "Advertiser.hpp"


namespace clipbird {
class LinuxBleAdvertiser final : public Advertiser {
 public:
  LinuxBleAdvertiser(const std::string& serviceUuid, const std::vector<std::uint8_t>& serviceData);
  ~LinuxBleAdvertiser() override = default;

  void startAdvertising() override;
  bool isAdvertising() const override;
  void stopAdvertising() override;

 private:
  sdbus::ObjectPath getAdvertisingManagerAdapterPath();

 private:
  const std::unique_ptr<sdbus::IConnection> connection;
  const std::unique_ptr<sdbus::IProxy> bluezProxy;
  const std::unique_ptr<LinuxBleAdvertisementData> advertisementData;

 private:
  bool advertising = false;

 private:
  inline static constexpr const char* kObjectManagerInterface = "org.freedesktop.DBus.ObjectManager";
  inline static constexpr const char* kBluezService = "org.bluez";
  inline static constexpr const char* kRootPath = "/";
  inline static constexpr const char* kAdapterInterface = "org.bluez.Adapter1";
  inline static constexpr const char* kAdvertisingManagerInterface = "org.bluez.LEAdvertisingManager1";
  inline static constexpr const char* kAdvertisementPath = "/com/srilakshmikanthanp/clipbird/advertisement0";
};
}  // namespace clipbird
