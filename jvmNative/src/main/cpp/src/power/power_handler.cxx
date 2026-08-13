#include "power/power_handler.hxx"

#include "error/Error.hpp"

#include <exception>

namespace error = clipbird::error;

extern "C" {

clipbird_power_handler_t* clipbird_power_handler_create(
  clipbird_power_handler_callback_t on_sleep,
  clipbird_power_handler_callback_t on_wake,
  void* context
) {
  if (!on_sleep || !on_wake) {
    error::setLastError(CLIPBIRD_POWER_HANDLER_INVALID_ARGUMENT, "on_sleep and on_wake must not be null.");
    return nullptr;
  }

  auto on_sleep_wrapper = [on_sleep, context]() { on_sleep(context); };
  auto on_wake_wrapper = [on_wake, context]() { on_wake(context); };

  try {
    auto impl = clipbird::power::createPowerHandler(std::move(on_sleep_wrapper), std::move(on_wake_wrapper));
    error::clearLastError();
    return new clipbird_power_handler{std::move(impl)};
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_POWER_HANDLER_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_POWER_HANDLER_INTERNAL_ERROR, "Unknown error creating power handler.");
    return nullptr;
  }
}

void clipbird_power_handler_destroy(clipbird_power_handler_t* handler) {
  delete handler;
}

}  // extern "C"
