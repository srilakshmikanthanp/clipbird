#pragma once

#include <memory>

#include "Channel.hpp"

namespace com::srilakshmikanthanp::clipbird::io {

class Server {
 public:
  virtual std::unique_ptr<Channel> accept() = 0;
  virtual void close() = 0;

  virtual ~Server() = default;
};

}  // namespace com::srilakshmikanthanp::clipbird
