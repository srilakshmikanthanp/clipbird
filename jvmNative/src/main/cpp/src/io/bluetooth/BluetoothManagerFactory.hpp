#pragma once

#include <memory>

#include "io/bluetooth/BluetoothManager.hpp"

namespace clipbird::io::bluetooth {
std::unique_ptr<BluetoothManager> createBluetoothManager();
}  // namespace clipbird::io::bluetooth
