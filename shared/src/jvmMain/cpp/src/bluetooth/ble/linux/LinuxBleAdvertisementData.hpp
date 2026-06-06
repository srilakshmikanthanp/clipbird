#pragma once

#include <cstdint>
#include <memory>
#include <string>
#include <vector>

#include <sdbus-c++/sdbus-c++.h>

namespace clipbird {
class LinuxBleAdvertisementData final {
 public:
  LinuxBleAdvertisementData(sdbus::IConnection& connection, sdbus::ObjectPath objectPath,
                            std::string serviceUuid, std::vector<std::uint8_t> serviceData,
                            std::function<void()> onRelease = []() {});
  ~LinuxBleAdvertisementData() = default;

  const sdbus::ObjectPath& getObjectPath() const;
  const std::string& getServiceUuid() const;
  const std::vector<std::uint8_t>& getServiceData() const;

 private:
  std::unique_ptr<sdbus::IObject> object;
  sdbus::ObjectPath objectPath;
  std::string serviceUuid;
  std::vector<std::uint8_t> serviceData;
  std::function<void()> onRelease;
};
}  // namespace clipbird
