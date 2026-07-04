#include "BluetoothManagerFactory.hpp"

#if defined(__linux__)
#include "linux/LinuxBluetoothManager.hpp"
#elif defined(_WIN32)
#include "windows/WindowsBluetoothManager.hpp"
#endif

namespace clipbird::bluetooth::rfcomm {

std::unique_ptr<BluetoothManager> createBluetoothManager() {
#if defined(__linux__)
  return std::make_unique<LinuxBluetoothManager>();
#elif defined(_WIN32)
  return std::make_unique<WindowsBluetoothManager>();
#else
  throw std::runtime_error("Unsupported platform for Bluetooth manager.");
#endif
}

}  // namespace clipbird::bluetooth::rfcomm
