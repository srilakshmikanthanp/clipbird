#include "WindowsBluetoothManager.hpp"

#include <stdexcept>

namespace clipbird::bluetooth::rfcomm {

std::vector<BluetoothDevice> WindowsBluetoothManager::bondedDevices() {
	throw std::runtime_error("WindowsBluetoothManager::bondedDevices() is not implemented yet.");
}

std::unique_ptr<io::Server> WindowsBluetoothManager::start(const std::string& serviceName, const std::string& serviceUuid) {
  throw std::runtime_error("WindowsBluetoothManager::start() is not implemented yet.");
}

std::unique_ptr<io::Channel> WindowsBluetoothManager::connect(const std::string& address, const std::string& serviceUuid) {
  throw std::runtime_error("WindowsBluetoothManager::connect() is not implemented yet.");
}

}  // namespace clipbird::bluetooth::rfcomm
