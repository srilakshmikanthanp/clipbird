#pragma once

#include <string>

#include "io/Channel.hpp"

namespace clipbird::io::bluetooth {

class BluetoothChannel : public io::Channel {
 public:
  virtual const std::string& remoteAddress() const = 0;
  ~BluetoothChannel() override = default;
};

}  // namespace clipbird::io::bluetooth
