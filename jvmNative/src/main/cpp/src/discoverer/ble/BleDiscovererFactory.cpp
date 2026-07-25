#include "discoverer/ble/BleDiscovererFactory.hpp"

#include <stdexcept>

#if defined(__linux__)
#include "linux/LinuxBleDiscoverer.hpp"
#endif

namespace clipbird::discoverer::ble {

std::unique_ptr<Discoverer> createBleDiscoverer(
  const boost::uuids::uuid& serviceUuid,
  DiscovererListener& listener
) {
#if defined(__linux__)
  return std::make_unique<LinuxBleDiscoverer>(serviceUuid, listener);
#elif defined(_WIN32)
  #error  "BLE discoverer is not implemented on Windows."
#else
  #error  "BLE discoverer is not implemented on this platform."
#endif
}

}  // namespace clipbird::discoverer::ble
