#pragma once

#include <memory>

#include "BluetoothManager.hpp"

namespace clipbird::bluetooth::rfcomm {
std::unique_ptr<BluetoothManager> createBluetoothManager();
}  // namespace clipbird
