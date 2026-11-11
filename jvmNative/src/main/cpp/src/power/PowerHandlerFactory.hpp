#pragma once

#include <functional>
#include <memory>

#include "power/PowerHandler.hpp"

namespace com::srilakshmikanthanp::clipbird::power {

std::unique_ptr<PowerHandler> createPowerHandler(std::function<void()> onSleep, std::function<void()> onWake);

}  // namespace com::srilakshmikanthanp::clipbird::power
