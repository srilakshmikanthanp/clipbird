#include "WindowsBleAdvertiser.hpp"

#include <stdexcept>

namespace clipbird::bluetooth::ble {
WindowsBleAdvertiser::WindowsBleAdvertiser(const boost::uuids::uuid& serviceUuid, const std::vector<std::uint8_t>& serviceData) {
  throw std::runtime_error("WindowsBleAdvertiser is not implemented yet.");
}

void WindowsBleAdvertiser::startAdvertising() {
  throw std::runtime_error("WindowsBleAdvertiser is not implemented yet.");
}

bool WindowsBleAdvertiser::isAdvertising() const {
  throw std::runtime_error("WindowsBleAdvertiser is not implemented yet.");
}

void WindowsBleAdvertiser::stopAdvertising() {
  throw std::runtime_error("WindowsBleAdvertiser is not implemented yet.");
}
}  // namespace clipbird
