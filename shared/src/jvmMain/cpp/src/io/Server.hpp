#pragma once

#include <memory>

#include "Channel.hpp"

namespace clipbird::io {

class Server {
 public:
  virtual ~Server() = default;

  virtual std::unique_ptr<Channel> accept() = 0;
  virtual void close() = 0;
};

}  // namespace clipbird
