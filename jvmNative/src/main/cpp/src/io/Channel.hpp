#pragma once

#include <cstddef>
#include <cstdint>
#include <vector>

namespace clipbird::io {

class Channel {
 public:
  virtual std::vector<std::uint8_t> readExactly(std::size_t size) = 0;
  virtual void write(const std::vector<std::uint8_t>& data) = 0;
  virtual void close() = 0;

  virtual ~Channel() = default;
};

}  // namespace clipbird
