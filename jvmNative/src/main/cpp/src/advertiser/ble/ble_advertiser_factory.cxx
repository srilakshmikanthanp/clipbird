#include "advertiser/ble/ble_advertiser_factory.hxx"

#include "advertiser/ble/BleAdvertiserFactory.hpp"
#include "error/Error.hpp"

#include <boost/uuid/string_generator.hpp>

#include <cstdint>
#include <exception>
#include <memory>
#include <string>
#include <vector>

namespace ble = clipbird::advertiser::ble;
namespace error = clipbird::error;

namespace {

struct CAdvertiserListener : clipbird::AdvertiserListener {
  clipbird_advertiser_ble_listener_t cbs;

  explicit CAdvertiserListener(const clipbird_advertiser_ble_listener_t& cbs) : cbs(cbs) {}

  void onAdvertisingStarted() override {
    if (cbs.on_started) cbs.on_started(cbs.context);
  }

  void onAdvertisingFailed(int code, const std::string& reason) override {
    if (cbs.on_failed) cbs.on_failed(code, reason.c_str(), cbs.context);
  }

  void onAdvertisingStopped() override {
    if (cbs.on_stopped) cbs.on_stopped(cbs.context);
  }
};

}  // namespace

extern "C" {

clipbird_advertiser_t* clipbird_advertiser_ble_create(const char* service_uuid, const std::uint8_t* service_data, int service_data_length, const clipbird_advertiser_ble_listener_t* events) {
  if (!service_uuid || !service_data || !events) {
    error::setLastError(clipbird_advertiser_ble_factory_error_code::CLIPBIRD_ADVERTISER_BLE_FACTORY_INTERNAL_ERROR, "Invalid argument: service_uuid, service_data and events must not be null.");
    return nullptr;
  }
  try {
    auto advertiserHandle = std::make_unique<clipbird_advertiser>();
    advertiserHandle->listener = std::make_unique<CAdvertiserListener>(*events);
    boost::uuids::string_generator uuidGenerator;
    auto serviceData = std::vector<std::uint8_t>(service_data, service_data + service_data_length);
    advertiserHandle->impl = ble::createBleAdvertiser(uuidGenerator(service_uuid), serviceData, *advertiserHandle->listener);
    error::clearLastError();
    return advertiserHandle.release();
  } catch (const std::exception& e) {
    error::setLastError(clipbird_advertiser_ble_factory_error_code::CLIPBIRD_ADVERTISER_BLE_FACTORY_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(clipbird_advertiser_ble_factory_error_code::CLIPBIRD_ADVERTISER_BLE_FACTORY_INTERNAL_ERROR, "Unknown error occurred while creating BLE advertiser.");
    return nullptr;
  }
}

}
