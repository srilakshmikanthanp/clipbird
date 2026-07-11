#pragma once

#include <memory>

#include "io/bluetooth/bluetooth_manager_factory.h"
#include "io/bluetooth/BluetoothManager.hpp"

struct clipbird_bluetooth_manager {
  std::unique_ptr<clipbird::io::bluetooth::BluetoothManager> impl;
};
