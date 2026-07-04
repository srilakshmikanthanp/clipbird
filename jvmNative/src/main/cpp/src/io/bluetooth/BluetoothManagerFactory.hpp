#pragma once

#include <memory>

#include "io/bluetooth/BluetoothManager.hpp"

namespace clipbird::bluetooth {
std::unique_ptr<BluetoothManager> createBluetoothManager();
}  // namespace clipbird::bluetooth
