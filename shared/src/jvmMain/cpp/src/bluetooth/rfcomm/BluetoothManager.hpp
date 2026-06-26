#pragma once

#include <memory>
#include <string>
#include <vector>

#include "BluetoothDevice.hpp"
#include "io/Channel.hpp"
#include "io/Server.hpp"

namespace clipbird::bluetooth::rfcomm {

class BluetoothManager {
 public:
  virtual ~BluetoothManager() = default;

  virtual std::vector<BluetoothDevice> bondedDevices() = 0;
  virtual std::unique_ptr<io::Server> start(const std::string& serviceName, const std::string& serviceUuid) = 0;
  virtual std::unique_ptr<io::Channel> connect(const std::string& address, const std::string& serviceUuid) = 0;
};

}  // namespace clipbird
