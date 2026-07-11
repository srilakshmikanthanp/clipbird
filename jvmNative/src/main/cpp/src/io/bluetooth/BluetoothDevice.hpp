#pragma once

#include <string>
#include <vector>

#include <boost/uuid/uuid.hpp>

namespace clipbird::io::bluetooth {

struct BluetoothDevice {
  std::string address;
  std::string name;
  std::vector<boost::uuids::uuid> serviceUuids;
};

}  // namespace clipbird::io::bluetooth
