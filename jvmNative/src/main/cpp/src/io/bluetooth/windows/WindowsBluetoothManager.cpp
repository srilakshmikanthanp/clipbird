#include "WindowsBluetoothManager.hpp"

#include <stdexcept>

namespace clipbird::io::bluetooth {

void WindowsBluetoothManager::setBondedDevicesChangedCallback(std::function<void()>) {
  throw std::runtime_error("WindowsBluetoothManager::setBondedDevicesChangedCallback() is not implemented yet.");
}

void WindowsBluetoothManager::removeBondedDevicesChangedCallback() {
  throw std::runtime_error("WindowsBluetoothManager::removeBondedDevicesChangedCallback() is not implemented yet.");
}

std::vector<bluetooth::BluetoothDevice> WindowsBluetoothManager::bondedDevices() {
	throw std::runtime_error("WindowsBluetoothManager::bondedDevices() is not implemented yet.");
}

std::string WindowsBluetoothManager::localName() {
	throw std::runtime_error("WindowsBluetoothManager::localName() is not implemented yet.");
}

std::unique_ptr<io::Channel> WindowsBluetoothManager::connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) {
  throw std::runtime_error("WindowsBluetoothManager::connectRfcomm() is not implemented yet.");
}

std::unique_ptr<io::Server> WindowsBluetoothManager::startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) {
  throw std::runtime_error("WindowsBluetoothManager::startRfcommServer() is not implemented yet.");
}

}  // namespace clipbird::io::bluetooth
