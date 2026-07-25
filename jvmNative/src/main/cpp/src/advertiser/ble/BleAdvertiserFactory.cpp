#include "advertiser/ble/BleAdvertiserFactory.hpp"

#if defined(__linux__)
#include "linux/LinuxBleAdvertiser.hpp"
#elif defined(_WIN32)
  #error "BLE advertiser is not implemented on Windows."
#else
  #error "BLE advertiser is not implemented on this platform."
#endif

namespace clipbird::advertiser::ble {

std::unique_ptr<Advertiser> createBleAdvertiser(
  const boost::uuids::uuid& serviceUuid,
  const std::vector<std::uint8_t>& serviceData,
  AdvertiserListener& events
) {
  return std::make_unique<LinuxBleAdvertiser>(serviceUuid, serviceData, events);
}

}  // namespace clipbird::advertiser::ble
