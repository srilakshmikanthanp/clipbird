#pragma once

#include <cstddef>
#include <functional>
#include <memory>
#include <string>
#include <vector>

#include <boost/uuid/uuid.hpp>

#include "io/bluetooth/BluetoothDevice.hpp"
#include "io/Channel.hpp"
#include "io/Server.hpp"

namespace clipbird::io::bluetooth {

class BluetoothManager {
 public:
  virtual ~BluetoothManager() = default;

  virtual void setBondedDevicesChangedCallback(std::function<void()> callback) = 0;
  virtual void removeBondedDevicesChangedCallback() = 0;

  virtual std::vector<BluetoothDevice> bondedDevices() = 0;
  virtual std::string localName() = 0;
  virtual std::unique_ptr<io::Channel> connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) = 0;
  virtual std::unique_ptr<io::Server> startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) = 0;
};

}  // namespace clipbird::io::bluetooth
