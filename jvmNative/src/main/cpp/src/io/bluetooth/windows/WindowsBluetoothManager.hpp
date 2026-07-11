#pragma once

#include "io/bluetooth/BluetoothManager.hpp"

namespace clipbird::io::bluetooth {

class WindowsBluetoothManager final : public BluetoothManager {
 public:
  std::vector<bluetooth::BluetoothDevice> bondedDevices() override;
  std::unique_ptr<io::Channel> connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) override;
  std::unique_ptr<io::Server> startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) override;
};

}  // namespace clipbird::io::bluetooth
