#include "io/bluetooth/BluetoothManagerFactory.hpp"

#if defined(__linux__)
#include "linux/LinuxBluetoothManager.hpp"
#elif defined(_WIN32)
  #error "Bluetooth manager is not implemented on Windows."
#else
  #error "Bluetooth manager is not implemented on this platform."
#endif

namespace clipbird::io::bluetooth {

std::unique_ptr<BluetoothManager> createBluetoothManager() {
  return std::make_unique<LinuxBluetoothManager>();
}

}  // namespace clipbird::io::bluetooth
