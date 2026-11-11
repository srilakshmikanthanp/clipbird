#pragma once

#include <string>
#include <vector>

#include <boost/uuid/uuid.hpp>

namespace com::srilakshmikanthanp::clipbird::io::bluetooth {

struct BluetoothDevice {
  std::string address;
  std::string name;
};

}  // namespace com::srilakshmikanthanp::clipbird::io::bluetooth
