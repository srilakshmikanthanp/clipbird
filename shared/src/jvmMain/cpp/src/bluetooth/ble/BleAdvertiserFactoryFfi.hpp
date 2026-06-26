#pragma once

#include <cstdint>

#include "advertiser/AdvertiserFfi.hpp"

namespace clipbird::bluetooth::ble {

/**
 * Enumeration of error codes for BLE advertiser factory operations.
 */
enum class ClipBirdAdvertiserFactoryErrorCode: int {
  FACTORY_INTERNAL_ERROR = 1
};

}  // namespace clipbird::bluetooth::ble

extern "C" {

/**
 * Creates a new BLE advertiser instance.
 * @param service_uuid The UUID of the BLE service to advertise.
 * @param service_data The service data to include in the advertisement.
 * @param service_data_length The length of the service data.
 * @return A pointer to the created ClipBirdAdvertiser instance, or nullptr on failure.
 */
clipbird::advertiser::ClipBirdAdvertiser* clipbird_ble_advertiser_create(const char* service_uuid, const std::uint8_t* service_data, int service_data_length);

}
