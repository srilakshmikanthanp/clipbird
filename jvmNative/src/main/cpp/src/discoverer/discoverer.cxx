#include <exception>
#include <memory>

#include "discoverer/discoverer.hxx"
#include "error/Error.hpp"

namespace error = clipbird::error;

extern "C" {

bool clipbird_discoverer_start(clipbird_discoverer_t* discoverer) {
  if (!discoverer || !discoverer->impl) {
    error::setLastError(CLIPBIRD_DISCOVERER_INVALID_ARGUMENT, "Invalid argument: discoverer must not be null.");
    return false;
  }

  try {
    discoverer->impl->startDiscovery();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_DISCOVERER_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_DISCOVERER_INTERNAL_ERROR, "Unknown error occurred while starting BLE discovery.");
    return false;
  }
}

bool clipbird_discoverer_stop(clipbird_discoverer_t* discoverer) {
  if (!discoverer || !discoverer->impl) {
    error::setLastError(CLIPBIRD_DISCOVERER_INVALID_ARGUMENT, "Invalid argument: discoverer must not be null.");
    return false;
  }

  try {
    discoverer->impl->stopDiscovery();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_DISCOVERER_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_DISCOVERER_INTERNAL_ERROR, "Unknown error occurred while stopping BLE discovery.");
    return false;
  }
}

void clipbird_discoverer_destroy(clipbird_discoverer_t* discoverer) {
  delete discoverer;
}

}
