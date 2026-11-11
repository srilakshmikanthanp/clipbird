#pragma once

#include <string>

#include "io/Channel.hpp"

namespace com::srilakshmikanthanp::clipbird::io::bluetooth {

class BluetoothChannel : public io::Channel {
 public:
  virtual const std::string& remoteAddress() const = 0;
  ~BluetoothChannel() override = default;
};

}  // namespace com::srilakshmikanthanp::clipbird::io::bluetooth
