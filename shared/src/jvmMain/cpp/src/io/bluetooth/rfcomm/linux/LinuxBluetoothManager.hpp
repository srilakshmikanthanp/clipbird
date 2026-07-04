#pragma once

#include <memory>
#include <stdexcept>
#include <string>
#include <vector>

#include "io/bluetooth/rfcomm/BluetoothManager.hpp"
#include "LinuxRfcommServer.hpp"
#include "LinuxRfcommChannel.hpp"

namespace clipbird::bluetooth::rfcomm {

class LinuxBluetoothManager final : public BluetoothManager {
 public:
  std::vector<BluetoothDevice> bondedDevices() override;
  std::unique_ptr<io::Server> start(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) override;
  std::unique_ptr<io::Channel> connect(const std::string& address, const boost::uuids::uuid& serviceUuid) override;

 private:
  std::unique_ptr<sdbus::IConnection> connection = sdbus::createSystemBusConnection();
};

}  // namespace clipbird::bluetooth::rfcomm
