#include <cstdint>
#include <exception>
#include <memory>

#include "advertiser/advertiser.hxx"
#include "error/Error.hpp"

namespace error = clipbird::error;

extern "C" {

bool clipbird_advertiser_start(clipbird_advertiser_t* advertiser) {
  if (!advertiser || !advertiser->impl) {
    error::setLastError(CLIPBIRD_ADVERTISER_INVALID_ARGUMENT, "Invalid argument: advertiser must not be null.");
    return false;
  }
  try {
    advertiser->impl->startAdvertising();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_ADVERTISER_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_ADVERTISER_INTERNAL_ERROR, "INTERNAL_ERROR error occurred while starting BLE advertising.");
    return false;
  }
}

bool clipbird_advertiser_stop(clipbird_advertiser_t* advertiser) {
  if (!advertiser || !advertiser->impl) {
    error::setLastError(CLIPBIRD_ADVERTISER_INVALID_ARGUMENT, "Invalid argument: advertiser must not be null.");
    return false;
  }
  try {
    advertiser->impl->stopAdvertising();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_ADVERTISER_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_ADVERTISER_INTERNAL_ERROR, "INTERNAL_ERROR error occurred while stopping BLE advertising.");
    return false;
  }
}

bool clipbird_advertiser_is_advertising(const clipbird_advertiser_t* advertiser, bool* is_advertising) {
  if (!advertiser || !advertiser->impl || !is_advertising) {
    error::setLastError(CLIPBIRD_ADVERTISER_INVALID_ARGUMENT, "Invalid argument: advertiser and is_advertising must not be null.");
    return false;
  }
  try {
    *is_advertising = advertiser->impl->isAdvertising();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_ADVERTISER_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_ADVERTISER_INTERNAL_ERROR, "INTERNAL_ERROR error occurred while checking BLE advertising status.");
    return false;
  }
}

void clipbird_advertiser_destroy(clipbird_advertiser_t* advertiser) {
  delete advertiser;
}

}
