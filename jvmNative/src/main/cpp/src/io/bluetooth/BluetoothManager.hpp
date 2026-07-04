#pragma once

#include <memory>
#include <string>
#include <vector>

#include <boost/uuid/uuid.hpp>

#include "io/bluetooth/BluetoothDevice.hpp"
#include "io/Channel.hpp"
#include "io/Server.hpp"

namespace clipbird::bluetooth {

class BluetoothManager {
 public:
  virtual ~BluetoothManager() = default;

  virtual std::vector<BluetoothDevice> bondedDevices() = 0;
  virtual std::unique_ptr<io::Channel> connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) = 0;
  virtual std::unique_ptr<io::Server> startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) = 0;
};

}  // namespace clipbird::bluetooth
