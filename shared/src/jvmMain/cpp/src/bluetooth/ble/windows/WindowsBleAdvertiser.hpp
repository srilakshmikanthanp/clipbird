#pragma once

#include <cstdint>
#include <string>
#include <vector>

#include "advertiser/Advertiser.hpp"

namespace clipbird::bluetooth::ble {
class WindowsBleAdvertiser : public Advertiser {
 public:
  WindowsBleAdvertiser(const std::string& serviceUuid, const std::vector<std::uint8_t>& serviceData);
  ~WindowsBleAdvertiser() override = default;
  void startAdvertising() override;
  bool isAdvertising() const override;
  void stopAdvertising() override;
};
}  // namespace clipbird
