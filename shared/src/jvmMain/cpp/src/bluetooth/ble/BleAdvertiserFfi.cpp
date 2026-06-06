#include "BleAdvertiserFfi.hpp"

namespace {
thread_local std::string g_last_error;

void set_last_error(const std::string& error) {
  g_last_error = error;
}

void clear_last_error() {
  g_last_error.clear();
}
}  // namespace

struct ClipBirdAdvertiser {
  std::unique_ptr<clipbird::Advertiser> impl;
};

extern "C" {
ClipBirdAdvertiser* clipbird_ble_advertiser_create(const char* service_uuid, const uint8_t* service_data, int service_data_length) {
  try {
    auto advertiser = std::make_unique<ClipBirdAdvertiser>();
    advertiser->impl = clipbird::createBleAdvertiser(std::string(service_uuid), std::vector<std::uint8_t>(service_data, service_data + service_data_length));
    clear_last_error();
    return advertiser.release();
  } catch (const std::exception& e) {
    set_last_error(e.what());
    return nullptr;
  } catch (...) {
    set_last_error("Unknown error occurred while creating BLE advertiser.");
    return nullptr;
  }
}

void clipbird_ble_advertiser_destroy(ClipBirdAdvertiser* advertiser) {
  delete advertiser;
}

int clipbird_ble_advertiser_start(ClipBirdAdvertiser* advertiser) {
  if (!advertiser) {
    set_last_error("Invalid argument: advertiser must not be null.");
    return -1;
  }
  try {
    advertiser->impl->startAdvertising();
    clear_last_error();
    return 0;
  } catch (const std::exception& e) {
    set_last_error(e.what());
    return -1;
  } catch (...) {
    set_last_error("Unknown error occurred while starting BLE advertising.");
    return -2;
  }
}

int clipbird_ble_advertiser_stop(ClipBirdAdvertiser* advertiser) {
  if (!advertiser) {
    set_last_error("Invalid argument: advertiser must not be null.");
    return -1;
  }
  try {
    advertiser->impl->stopAdvertising();
    clear_last_error();
    return 0;
  } catch (const std::exception& e) {
    set_last_error(e.what());
    return -1;
  } catch (...) {
    set_last_error("Unknown error occurred while stopping BLE advertising.");
    return -2;
  }
}

int clipbird_ble_advertiser_is_advertising(const ClipBirdAdvertiser* advertiser, bool* is_advertising) {
  if (!advertiser || !is_advertising) {
    set_last_error("Invalid argument: advertiser and is_advertising must not be null.");
    return -1;
  }
  try {
    *is_advertising = advertiser->impl->isAdvertising();
    clear_last_error();
    return 0;
  } catch (const std::exception& e) {
    set_last_error(e.what());
    return -1;
  } catch (...) {
    set_last_error("Unknown error occurred while checking advertising status.");
    return -2;
  }
}

const char* clipbird_ble_advertiser_last_error() {
  return g_last_error.c_str();
}
}
