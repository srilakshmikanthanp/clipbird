#pragma once

#include <vector>

#include "io/bluetooth/bluetooth_manager.h"
#include "io/bluetooth/bluetooth_manager_factory.hxx"
#include "io/bluetooth/BluetoothManager.hpp"
#include "io/channel.hxx"
#include "io/server.hxx"

struct clipbird_bluetooth_device_list {
  std::vector<clipbird::io::bluetooth::BluetoothDevice> devices;
};
