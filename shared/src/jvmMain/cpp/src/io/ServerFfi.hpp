#pragma once

#include <cstdint>
#include <memory>

#include "io/ChannelFfi.hpp"
#include "io/Server.hpp"

namespace clipbird::io {

struct ClipBirdServer {
  std::unique_ptr<Server> impl;
};

}  // namespace clipbird::io

extern "C" {

clipbird::io::ClipBirdChannel* clipbird_server_accept(clipbird::io::ClipBirdServer* server);
void clipbird_server_destroy(clipbird::io::ClipBirdServer* server);

}
