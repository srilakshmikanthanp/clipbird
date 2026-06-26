#pragma once

#include "io/Server.hpp"

namespace clipbird::bluetooth::rfcomm {

class LinuxRfcommServer final : public io::Server {
 public:
  explicit LinuxRfcommServer();
  std::unique_ptr<io::Channel> accept() override;
  ~LinuxRfcommServer();
};

}  // namespace clipbird::bluetooth::rfcomm
