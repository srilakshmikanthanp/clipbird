#pragma once

#include <functional>

namespace com::srilakshmikanthanp::clipbird::power {

class PowerHandler {
 protected:
  std::function<void()> onSleepCallback;
  std::function<void()> onWakeCallback;

 public:
  PowerHandler(std::function<void()> onSleep, std::function<void()> onWake): onSleepCallback(std::move(onSleep)), onWakeCallback(std::move(onWake)) {}
  virtual ~PowerHandler() = default;
};

}  // namespace com::srilakshmikanthanp::clipbird::power
