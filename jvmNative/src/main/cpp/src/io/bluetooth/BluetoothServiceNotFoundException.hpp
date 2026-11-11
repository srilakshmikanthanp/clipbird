#pragma once

#include "io/IOException.hpp"

namespace com::srilakshmikanthanp::clipbird::io::bluetooth {
class BluetoothServiceNotFoundException : public IOException {
 public:
  explicit BluetoothServiceNotFoundException(const std::string& message) : IOException(message) {}
};
}  // namespace com::srilakshmikanthanp::clipbird::io
