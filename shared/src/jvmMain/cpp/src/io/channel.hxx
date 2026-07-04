#pragma once

#include <memory>

#include "io/Channel.hpp"
#include "io/channel.h"

struct clipbird_io_channel {
  std::unique_ptr<clipbird::io::Channel> impl;
};
