#pragma once

#include <cstdint>
#include <vector>

#include <boost/uuid/uuid.hpp>

#include "advertiser/Advertiser.hpp"
#include "advertiser/AdvertiserEvents.hpp"

namespace clipbird::advertiser::ble {

class WindowsBleAdvertiser : public Advertiser {
 public:
  WindowsBleAdvertiser(const boost::uuids::uuid& serviceUuid, const std::vector<std::uint8_t>& serviceData, AdvertiserListener& listener);
  ~WindowsBleAdvertiser() override = default;
  void startAdvertising() override;
  void stopAdvertising() override;
};

}  // namespace clipbird::advertiser::ble
