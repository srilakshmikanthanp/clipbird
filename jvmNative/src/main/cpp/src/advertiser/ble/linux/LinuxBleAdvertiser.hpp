#pragma once

#include <atomic>
#include <cstdint>
#include <memory>
#include <vector>

#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

#include "LinuxBleAdvertisementData.hpp"
#include "advertiser/Advertiser.hpp"
#include "advertiser/AdvertiserListener.hpp"

namespace clipbird::advertiser::ble {

class LinuxBleAdvertiser final : public Advertiser {
 public:
  LinuxBleAdvertiser(const boost::uuids::uuid& serviceUuid, const std::vector<std::uint8_t>& serviceData, AdvertiserListener& listener);
  ~LinuxBleAdvertiser() override;

  void startAdvertising() override;
  void stopAdvertising() override;

 private:
  sdbus::ObjectPath getAdvertisingManagerAdapterPath();
  void onReleaseAdvertisement();
  void onAdapterPropertiesChanged(const std::string& ifaceName, const std::map<std::string, sdbus::Variant>& changedProps, const std::vector<std::string>& invalidated);

 private:
  const std::unique_ptr<sdbus::IConnection> connection;
  const std::unique_ptr<sdbus::IProxy> bluezProxy;
  std::unique_ptr<sdbus::IProxy> advertisingManagerProxy;
  std::unique_ptr<LinuxBleAdvertisementData> data;

 private:
  std::atomic<bool> advertising{false};

 private:
  inline static constexpr const char* kObjectManagerInterface = "org.freedesktop.DBus.ObjectManager";
  inline static constexpr const char* kBluezService = "org.bluez";
  inline static constexpr const char* kRootPath = "/";
  inline static constexpr const char* kAdapterInterface = "org.bluez.Adapter1";
  inline static constexpr const char* kAdvertisingManagerInterface = "org.bluez.LEAdvertisingManager1";
  inline static constexpr const char* kAdvertisementPath = "/com/srilakshmikanthanp/clipbird/advertisement";
};

}  // namespace clipbird::advertiser::ble
