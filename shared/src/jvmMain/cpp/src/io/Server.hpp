#pragma once

#include <memory>

#include "Channel.hpp"

namespace clipbird::io {

class Server {
 public:
  virtual std::unique_ptr<Channel> accept() = 0;
  virtual ~Server() = default;
};

}  // namespace clipbird
