#include "bluetooth/ble/BleAdvertiserFactoryFfi.hpp"
#include "bluetooth/ble/BleAdvertiserFactory.hpp"
#include "error/Error.hpp"

#include <boost/uuid/string_generator.hpp>

#include <cstdint>
#include <exception>
#include <memory>
#include <vector>

namespace advertiser = clipbird::advertiser;
namespace ble = clipbird::bluetooth::ble;
namespace error = clipbird::error;
namespace utility = clipbird::utility;

extern "C" {

advertiser::ClipBirdAdvertiser* clipbird_ble_advertiser_create(const char* service_uuid, const std::uint8_t* service_data, int service_data_length) {
  try {
    boost::uuids::string_generator uuidGenerator;
    auto advertiser = std::make_unique<advertiser::ClipBirdAdvertiser>();
    advertiser->impl = ble::createBleAdvertiser(uuidGenerator(service_uuid), std::vector<std::uint8_t>(service_data, service_data + service_data_length));
    error::clearLastError();
    return advertiser.release();
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(ble::ClipBirdAdvertiserFactoryErrorCode::FACTORY_INTERNAL_ERROR), e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(utility::toUnderlying(ble::ClipBirdAdvertiserFactoryErrorCode::FACTORY_INTERNAL_ERROR), "Unknown error occurred while creating BLE advertiser.");
    return nullptr;
  }
}

}
