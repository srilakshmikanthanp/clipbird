#pragma once

#include "io/bluetooth/BluetoothManager.hpp"

namespace clipbird::io::bluetooth {

class WindowsBluetoothManager final : public BluetoothManager {
 public:
  void setBondedDevicesChangedCallback(std::function<void()> callback) override;
  void removeBondedDevicesChangedCallback() override;

  std::vector<bluetooth::BluetoothDevice> bondedDevices() override;
  std::string localName() override;
  std::unique_ptr<io::Channel> connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) override;
  std::unique_ptr<io::Server> startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) override;
};

}  // namespace clipbird::io::bluetooth
