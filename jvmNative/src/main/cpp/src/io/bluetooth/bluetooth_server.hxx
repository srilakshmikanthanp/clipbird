#pragma once

#include <memory>

#include "io/Server.hpp"
#include "io/bluetooth/bluetooth_channel.hxx"
#include "io/bluetooth/bluetooth_server.h"

struct clipbird_io_bluetooth_server {
  std::unique_ptr<clipbird::io::Server> impl;
};
