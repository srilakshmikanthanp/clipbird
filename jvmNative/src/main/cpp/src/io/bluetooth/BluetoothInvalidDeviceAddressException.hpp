#pragma once

#include "io/IOException.hpp"

namespace com::srilakshmikanthanp::clipbird::io::bluetooth {
class BluetoothInvalidDeviceAddressException : public IOException {
 public:
  explicit BluetoothInvalidDeviceAddressException(const std::string& message) : IOException(message) {}
};
}  // namespace com::srilakshmikanthanp::clipbird::io
