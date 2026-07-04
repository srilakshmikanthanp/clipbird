#pragma once

#include <cstdint>
#include <vector>

#include <boost/uuid/uuid.hpp>

#include "advertiser/Advertiser.hpp"

namespace clipbird::advertiser::ble {
class WindowsBleAdvertiser : public Advertiser {
 public:
  WindowsBleAdvertiser(const boost::uuids::uuid& serviceUuid, const std::vector<std::uint8_t>& serviceData);
  ~WindowsBleAdvertiser() override = default;
  void startAdvertising() override;
  bool isAdvertising() const override;
  void stopAdvertising() override;
};
}  // namespace clipbird
