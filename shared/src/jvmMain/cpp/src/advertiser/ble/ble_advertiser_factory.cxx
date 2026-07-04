#include "advertiser/ble/ble_advertiser_factory.hxx"

#include "advertiser/ble/BleAdvertiserFactory.hpp"
#include "error/Error.hpp"

#include <boost/uuid/string_generator.hpp>

#include <cstdint>
#include <exception>
#include <memory>
#include <vector>

namespace ble = clipbird::advertiser::ble;
namespace error = clipbird::error;

extern "C" {

clipbird_advertiser_t* clipbird_advertiser_ble_create(const char* service_uuid, const std::uint8_t* service_data, int service_data_length) {
  try {
    boost::uuids::string_generator uuidGenerator;
    auto advertiserHandle = std::make_unique<clipbird_advertiser>();
    advertiserHandle->impl = ble::createBleAdvertiser(uuidGenerator(service_uuid), std::vector<std::uint8_t>(service_data, service_data + service_data_length));
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
