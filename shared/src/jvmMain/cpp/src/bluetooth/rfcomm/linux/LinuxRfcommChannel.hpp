#pragma once

#include "io/Channel.hpp"

namespace clipbird::bluetooth::rfcomm {

class LinuxRfcommChannel final : public io::Channel {
 public:
  explicit LinuxRfcommChannel();

  std::vector<std::uint8_t> readExactly(std::size_t size) override;
  void write(const std::vector<std::uint8_t>& data) override;
  bool isOpen() const override;
  void close() override;
};

}  // namespace clipbird::bluetooth::rfcomm
