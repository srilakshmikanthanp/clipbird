#include <cstdint>
#include <exception>
#include <memory>

#include "advertiser/AdvertiserFfi.hpp"
#include "error/Error.hpp"

namespace advertiser = clipbird::advertiser;
namespace error = clipbird::error;
namespace utility = clipbird::utility;

extern "C" {

bool clipbird_ble_advertiser_start(advertiser::ClipBirdAdvertiser* advertiser) {
  if (!advertiser) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), "Invalid argument: advertiser must not be null.");
    return false;
  }
  try {
    advertiser->impl->startAdvertising();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), e.what());
    return false;
  } catch (...) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), "INTERNAL_ERROR error occurred while starting BLE advertising.");
    return false;
  }
}

bool clipbird_ble_advertiser_stop(advertiser::ClipBirdAdvertiser* advertiser) {
  if (!advertiser) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), "Invalid argument: advertiser must not be null.");
    return false;
  }
  try {
    advertiser->impl->stopAdvertising();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), e.what());
    return false;
  } catch (...) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), "INTERNAL_ERROR error occurred while stopping BLE advertising.");
    return false;
  }
}

bool clipbird_ble_advertiser_is_advertising(const advertiser::ClipBirdAdvertiser* advertiser, bool* is_advertising) {
  if (!advertiser || !is_advertising) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), "Invalid argument: advertiser and is_advertising must not be null.");
    return false;
  }
  try {
    *is_advertising = advertiser->impl->isAdvertising();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), e.what());
    return false;
  } catch (...) {
    error::setLastError(utility::toUnderlying(advertiser::ClipBirdAdvertiserErrorCode::INTERNAL_ERROR), "INTERNAL_ERROR error occurred while checking BLE advertising status.");
    return false;
  }
}

void clipbird_ble_advertiser_destroy(advertiser::ClipBirdAdvertiser* advertiser) {
  delete advertiser;
}

}
