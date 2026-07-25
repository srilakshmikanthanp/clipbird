#pragma once

#include <cstdint>
#include <memory>
#include <vector>

#include <boost/uuid/uuid.hpp>

#include "advertiser/Advertiser.hpp"
#include "advertiser/AdvertiserListener.hpp"

namespace clipbird::advertiser::ble {

std::unique_ptr<Advertiser> createBleAdvertiser(
  const boost::uuids::uuid& serviceUuid,
  const std::vector<std::uint8_t>& serviceData,
  AdvertiserListener& listener
);

}  // namespace clipbird::advertiser::ble
