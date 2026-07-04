#include "WindowsBluetoothManager.hpp"

#include <stdexcept>

namespace clipbird::bluetooth {

std::vector<bluetooth::BluetoothDevice> WindowsBluetoothManager::bondedDevices() {
	throw std::runtime_error("WindowsBluetoothManager::bondedDevices() is not implemented yet.");
}

std::unique_ptr<io::Channel> WindowsBluetoothManager::connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) {
  throw std::runtime_error("WindowsBluetoothManager::connectRfcomm() is not implemented yet.");
}

std::unique_ptr<io::Server> WindowsBluetoothManager::startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) {
  throw std::runtime_error("WindowsBluetoothManager::startRfcommServer() is not implemented yet.");
}

}  // namespace clipbird::bluetooth
