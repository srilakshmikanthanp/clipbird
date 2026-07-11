#pragma once

#include "io/IOException.hpp"

namespace clipbird::io::bluetooth {
class BluetoothDeviceNotFoundException : public IOException {
 public:
  explicit BluetoothDeviceNotFoundException(const std::string& message) : IOException(message) {}
};
}  // namespace clipbird::io
