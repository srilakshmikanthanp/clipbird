#pragma once

#include <string>
#include <vector>

namespace clipbird::bluetooth::rfcomm {

struct BluetoothDevice {
  std::string address;
  std::string name;
  std::vector<std::string> serviceUuids;
};

}  // namespace clipbird
