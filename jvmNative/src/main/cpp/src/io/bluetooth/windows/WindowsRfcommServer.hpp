#pragma once

#include "io/Server.hpp"

namespace clipbird::io::bluetooth {

class WindowsRfcommServer final : public io::Server {
 public:
  explicit WindowsRfcommServer();

  std::unique_ptr<io::Channel> accept() override;
  void close() override;
};

}  // namespace clipbird::io::bluetooth
