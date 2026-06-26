#include "LinuxBluetoothManager.hpp"

namespace clipbird::bluetooth::rfcomm {

std::vector<BluetoothDevice> LinuxBluetoothManager::bondedDevices() {
	// TODO: Add implementation.
}

std::unique_ptr<io::Server> LinuxBluetoothManager::start(const std::string& serviceName, const std::string& serviceUuid) {
	// TODO: Add implementation.
}

std::unique_ptr<io::Channel> LinuxBluetoothManager::connect(const std::string& address, const std::string& serviceUuid) {
	// TODO: Add implementation.
}

}  // namespace clipbird::bluetooth::rfcomm
