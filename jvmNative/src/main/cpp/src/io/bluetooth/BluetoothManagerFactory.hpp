#pragma once

#include <memory>

#include "io/bluetooth/BluetoothManager.hpp"

namespace com::srilakshmikanthanp::clipbird::io::bluetooth {
std::unique_ptr<BluetoothManager> createBluetoothManager();
}  // namespace com::srilakshmikanthanp::clipbird::io::bluetooth
