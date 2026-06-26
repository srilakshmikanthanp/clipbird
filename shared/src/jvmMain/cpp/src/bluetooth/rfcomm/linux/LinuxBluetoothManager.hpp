#pragma once

#include "bluetooth/rfcomm/BluetoothManager.hpp"

namespace clipbird::bluetooth::rfcomm {

class LinuxBluetoothManager final : public BluetoothManager {
 public:
  std::vector<BluetoothDevice> bondedDevices() override;
  std::unique_ptr<io::Server> start(const std::string& serviceName, const std::string& serviceUuid) override;
  std::unique_ptr<io::Channel> connect(const std::string& address, const std::string& serviceUuid) override;
};

}  // namespace clipbird::bluetooth::rfcomm
