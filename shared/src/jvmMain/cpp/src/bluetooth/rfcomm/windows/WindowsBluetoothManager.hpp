#pragma once

#include "bluetooth/rfcomm/BluetoothManager.hpp"

namespace clipbird::bluetooth::rfcomm {

class WindowsBluetoothManager final : public BluetoothManager {
 public:
  std::vector<BluetoothDevice> bondedDevices() override;
  std::unique_ptr<io::Server> start(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) override;
  std::unique_ptr<io::Channel> connect(const std::string& address, const boost::uuids::uuid& serviceUuid) override;
};

}  // namespace clipbird::bluetooth::rfcomm
