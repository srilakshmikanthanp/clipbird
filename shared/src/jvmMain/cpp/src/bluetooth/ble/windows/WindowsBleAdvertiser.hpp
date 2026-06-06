#pragma once

#include <cstdint>
#include <string>
#include <vector>

#include "Advertiser.hpp"

namespace clipbird {
class WindowsBleAdvertiser : public Advertiser {
 public:
  WindowsBleAdvertiser(const std::string& serviceUuid, const std::vector<std::uint8_t>& serviceData);
  ~WindowsBleAdvertiser() override = default;
  void startAdvertising() override;
  bool isAdvertising() const override;
  void stopAdvertising() override;
};
}  // namespace clipbird
