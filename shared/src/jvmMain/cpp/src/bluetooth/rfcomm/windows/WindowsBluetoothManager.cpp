#include "WindowsBluetoothManager.hpp"

namespace clipbird::bluetooth::rfcomm {

std::vector<BluetoothDevice> WindowsBluetoothManager::bondedDevices() {
	// TODO: Add implementation.
}

std::unique_ptr<io::Server> WindowsBluetoothManager::start(const std::string& serviceName, const std::string& serviceUuid) {
	// TODO: Add implementation.
}

std::unique_ptr<io::Channel> WindowsBluetoothManager::connect(const std::string& address, const std::string& serviceUuid) {
	// TODO: Add implementation.
}

}  // namespace clipbird::bluetooth::rfcomm
