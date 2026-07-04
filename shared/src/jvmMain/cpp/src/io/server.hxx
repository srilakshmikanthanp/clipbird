#pragma once

#include <memory>

#include "io/Server.hpp"
#include "io/channel.hxx"
#include "io/server.h"

struct clipbird_io_server {
  std::unique_ptr<clipbird::io::Server> impl;
};
