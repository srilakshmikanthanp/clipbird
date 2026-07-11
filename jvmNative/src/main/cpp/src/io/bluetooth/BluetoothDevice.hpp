#pragma once

#include <string>
#include <vector>

#include <boost/uuid/uuid.hpp>

namespace clipbird::io::bluetooth {

struct BluetoothDevice {
  std::string address;
  std::string name;
};

}  // namespace clipbird::io::bluetooth
