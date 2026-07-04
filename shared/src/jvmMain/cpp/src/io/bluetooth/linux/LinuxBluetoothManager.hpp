#pragma once

#include <memory>
#include <stdexcept>
#include <string>
#include <vector>

#include "io/bluetooth/BluetoothManager.hpp"
#include "LinuxRfcommServer.hpp"
#include "LinuxRfcommChannel.hpp"

namespace clipbird::bluetooth {

class LinuxBluetoothManager final : public BluetoothManager {
 public:
  std::vector<bluetooth::BluetoothDevice> bondedDevices() override;
  std::unique_ptr<io::Channel> connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) override;
  std::unique_ptr<io::Server> startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) override;

 private:
  std::unique_ptr<sdbus::IConnection> connection = sdbus::createSystemBusConnection();
};

}  // namespace clipbird::bluetooth
