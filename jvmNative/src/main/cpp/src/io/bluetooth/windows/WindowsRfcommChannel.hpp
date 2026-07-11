#pragma once

#include <cstdint>

#include "io/Channel.hpp"

namespace clipbird::io::bluetooth {

class WindowsRfcommChannel final : public io::Channel {
 public:
  explicit WindowsRfcommChannel();

  std::vector<std::uint8_t> readExactly(std::size_t size) override;
  void write(const std::vector<std::uint8_t>& data) override;
  ~WindowsRfcommChannel() override;
};

}  // namespace clipbird::io::bluetooth
