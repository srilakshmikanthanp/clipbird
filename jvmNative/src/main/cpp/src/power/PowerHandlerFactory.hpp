#pragma once

#include <functional>
#include <memory>

#include "power/PowerHandler.hpp"

namespace clipbird::power {

std::unique_ptr<PowerHandler> createPowerHandler(std::function<void()> onSleep, std::function<void()> onWake);

}  // namespace clipbird::power
