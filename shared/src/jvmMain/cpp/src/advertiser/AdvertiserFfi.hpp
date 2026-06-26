#pragma once

#include <cstdint>
#include <memory>
#include "advertiser/Advertiser.hpp"
#include "utility/utility.hpp"

namespace clipbird::advertiser {

/**
 * Enumeration of error codes for BLE advertiser operations.
 */
enum class ClipBirdAdvertiserErrorCode: int {
  INVALID_ARGUMENT = 0,
  INTERNAL_ERROR = 1
};

/**
 * Struct representing a BLE advertiser instance to hold the implementation of the Advertiser interface.
 */
struct ClipBirdAdvertiser {
  std::unique_ptr<clipbird::Advertiser> impl;
};

}

extern "C" {
/**
 * Checks if the specified advertiser instance is currently advertising.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to check.
 * @param is_advertising A pointer to a boolean variable that will be set to true if advertising, or false otherwise.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_ble_advertiser_is_advertising(const clipbird::advertiser::ClipBirdAdvertiser* advertiser, bool* is_advertising);

/**
 * Starts BLE advertising using the specified advertiser instance.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to use for advertising.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_ble_advertiser_start(clipbird::advertiser::ClipBirdAdvertiser* advertiser);

/**
 * Stops BLE advertising for the specified advertiser instance.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to stop advertising.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_ble_advertiser_stop(clipbird::advertiser::ClipBirdAdvertiser* advertiser);

/**
 * Destroys a BLE advertiser instance.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to destroy.
 */
void clipbird_ble_advertiser_destroy(clipbird::advertiser::ClipBirdAdvertiser* advertiser);

}
