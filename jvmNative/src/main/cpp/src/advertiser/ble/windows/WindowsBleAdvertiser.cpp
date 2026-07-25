#include "WindowsBleAdvertiser.hpp"

#include <stdexcept>

namespace clipbird::advertiser::ble {

WindowsBleAdvertiser::WindowsBleAdvertiser(
  const boost::uuids::uuid& serviceUuid,
  const std::vector<std::uint8_t>& serviceData,
  AdvertiserListener& listener
) : Advertiser(listener) {
  throw std::runtime_error("WindowsBleAdvertiser is not implemented yet.");
}

void WindowsBleAdvertiser::startAdvertising() {
  throw std::runtime_error("WindowsBleAdvertiser is not implemented yet.");
}

void WindowsBleAdvertiser::stopAdvertising() {
  throw std::runtime_error("WindowsBleAdvertiser is not implemented yet.");
}

}  // namespace clipbird::advertiser::ble
