#include "advertiser/ble/BleAdvertiserFactory.hpp"

#include <stdexcept>

#if defined(__linux__)
#include "linux/LinuxBleAdvertiser.hpp"
#elif defined(_WIN32)
#include "windows/WindowsBleAdvertiser.hpp"
#endif


namespace clipbird::bluetooth::ble {

std::unique_ptr<Advertiser> createBleAdvertiser(const boost::uuids::uuid& serviceUuid, const std::vector<std::uint8_t>& serviceData) {
#if defined(__linux__)
  return std::make_unique<LinuxBleAdvertiser>(serviceUuid, serviceData);
#elif defined(_WIN32)
  return std::make_unique<WindowsBleAdvertiser>(serviceUuid, serviceData);
#else
  throw std::runtime_error("Unsupported platform for BLE advertiser.");
#endif
}

}  // namespace clipbird
