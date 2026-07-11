#pragma once

#include "io/IOException.hpp"

namespace clipbird::io::bluetooth {
class BluetoothInvalidDeviceAddressException : public IOException {
 public:
  explicit BluetoothInvalidDeviceAddressException(const std::string& message) : IOException(message) {}
};
}  // namespace clipbird::io
