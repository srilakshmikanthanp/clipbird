#pragma once

#include "io/Channel.hpp"

namespace clipbird::io::bluetooth {

class LinuxRfcommChannel final : public io::Channel {
 public:
  explicit LinuxRfcommChannel(int socket_fd);

  std::vector<std::uint8_t> readExactly(std::size_t size) override;
  void write(const std::vector<std::uint8_t>& data) override;
  ~LinuxRfcommChannel() override;

 private:
  int socket_fd;
};

}  // namespace clipbird::io::bluetooth
