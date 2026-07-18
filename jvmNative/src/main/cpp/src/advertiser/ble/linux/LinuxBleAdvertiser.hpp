#pragma once

#include <cstdint>
#include <memory>
#include <vector>

#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

#include "LinuxBleAdvertisementData.hpp"
#include "advertiser/Advertiser.hpp"


namespace clipbird::advertiser::ble {
class LinuxBleAdvertiser final : public Advertiser {
 public:
  LinuxBleAdvertiser(const boost::uuids::uuid& serviceUuid, const std::vector<std::uint8_t>& serviceData);
  ~LinuxBleAdvertiser() override;

  void startAdvertising() override;
  bool isAdvertising() const override;
  void stopAdvertising() override;

 private:
  sdbus::ObjectPath getAdvertisingManagerAdapterPath();

 private:
  const std::unique_ptr<sdbus::IConnection> connection;
  const std::unique_ptr<sdbus::IProxy> bluezProxy;
  std::unique_ptr<LinuxBleAdvertisementData> advertisementData;

 private:
  bool advertising = false;

 private:
  inline static constexpr const char* kObjectManagerInterface = "org.freedesktop.DBus.ObjectManager";
  inline static constexpr const char* kBluezService = "org.bluez";
  inline static constexpr const char* kRootPath = "/";
  inline static constexpr const char* kAdapterInterface = "org.bluez.Adapter1";
  inline static constexpr const char* kAdvertisingManagerInterface = "org.bluez.LEAdvertisingManager1";
  inline static constexpr const char* kAdvertisementPath = "/com/srilakshmikanthanp/clipbird/advertisement";
};
}  // namespace clipbird
