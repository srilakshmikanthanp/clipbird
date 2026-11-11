#pragma once

#include "IOException.hpp"

namespace com::srilakshmikanthanp::clipbird::io {
class EOFException : public IOException {
 public:
  explicit EOFException(const std::string& message) : IOException(message) {}
};
}  // namespace com::srilakshmikanthanp::clipbird::io
