#pragma once

#include "io/IOException.hpp"

namespace com::srilakshmikanthanp::clipbird::io::bluetooth {
class BluetoothDeviceNotFoundException : public IOException {
 public:
  explicit BluetoothDeviceNotFoundException(const std::string& message) : IOException(message) {}
};
}  // namespace com::srilakshmikanthanp::clipbird::io
