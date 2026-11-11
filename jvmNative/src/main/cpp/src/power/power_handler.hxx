#pragma once

#include <memory>

#include "power/power_handler.h"
#include "power/PowerHandler.hpp"
#include "power/PowerHandlerFactory.hpp"

struct clipbird_power_handler {
  std::unique_ptr<com::srilakshmikanthanp::clipbird::power::PowerHandler> impl;
};
