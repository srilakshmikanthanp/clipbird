#pragma once

#include <cstdint>
#include <memory>
#include <string>
#include <vector>

#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

namespace clipbird::advertiser::ble {
class LinuxBleAdvertisementData final {
 public:
  LinuxBleAdvertisementData(sdbus::IConnection& connection, sdbus::ObjectPath objectPath,
                            boost::uuids::uuid serviceUuid, std::vector<std::uint8_t> serviceData,
                            std::function<void()> onRelease = []() {});
  ~LinuxBleAdvertisementData() = default;

  const sdbus::ObjectPath& getObjectPath() const;
  const boost::uuids::uuid& getServiceUuid() const;
  const std::vector<std::uint8_t>& getServiceData() const;

 private:
  sdbus::ObjectPath objectPath;
  boost::uuids::uuid serviceUuid;
  std::vector<std::uint8_t> serviceData;
  std::function<void()> onRelease;
  std::unique_ptr<sdbus::IObject> object;
};
}  // namespace clipbird
