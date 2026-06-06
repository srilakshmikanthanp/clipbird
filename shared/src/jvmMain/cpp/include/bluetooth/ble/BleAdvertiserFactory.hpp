#pragma once

#include <cstdint>
#include <memory>
#include <string>
#include <vector>

#include "Advertiser.hpp"

namespace clipbird {
std::unique_ptr<Advertiser> createBleAdvertiser(const std::string& serviceUuid, const std::vector<std::uint8_t>& serviceData);
}  // namespace clipbird
