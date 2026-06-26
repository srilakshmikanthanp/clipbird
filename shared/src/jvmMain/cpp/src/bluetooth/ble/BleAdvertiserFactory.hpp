#pragma once

#include <cstdint>
#include <memory>
#include <string>
#include <vector>

#include "advertiser/Advertiser.hpp"

namespace clipbird::bluetooth::ble {
std::unique_ptr<Advertiser> createBleAdvertiser(const std::string& serviceUuid, const std::vector<std::uint8_t>& serviceData);
}  // namespace clipbird
