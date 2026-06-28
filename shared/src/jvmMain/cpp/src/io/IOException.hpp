#pragma once

#include <stdexcept>

namespace clipbird::io {
class IOException : public std::runtime_error {
 public:
  explicit IOException(const std::string& message) : std::runtime_error(message) {}
};
}  // namespace clipbird::io
