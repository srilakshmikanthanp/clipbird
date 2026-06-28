#pragma once

#include "IOException.hpp"

namespace clipbird::io {
class EOFException : public IOException {
 public:
  explicit EOFException(const std::string& message) : IOException(message) {}
};
}  // namespace clipbird::io
