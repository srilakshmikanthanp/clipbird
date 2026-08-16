#pragma once

#include <atomic>
#include <string>

#include "io/bluetooth/BluetoothChannel.hpp"

namespace clipbird::io::bluetooth {

class LinuxRfcommChannel final : public BluetoothChannel {
 public:
  LinuxRfcommChannel(int socket_fd, std::string remoteAddress);

  std::vector<std::uint8_t> readExactly(std::size_t size) override;
  void write(const std::vector<std::uint8_t>& data) override;
  const std::string& remoteAddress() const override;
  void close() override;
  ~LinuxRfcommChannel() override;

 private:
  int socket_fd;
  std::string remote_address;
  std::atomic<bool> shutdown_done{false};
};

}  // namespace clipbird::io::bluetooth
