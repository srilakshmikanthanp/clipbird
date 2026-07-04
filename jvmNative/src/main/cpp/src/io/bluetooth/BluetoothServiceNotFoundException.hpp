#pragma once

#include "io/IOException.hpp"

namespace clipbird::io::bluetooth {
class BluetoothServiceNotFoundException : public IOException {
 public:
  explicit BluetoothServiceNotFoundException(const std::string& message) : IOException(message) {}
};
}  // namespace clipbird::io
