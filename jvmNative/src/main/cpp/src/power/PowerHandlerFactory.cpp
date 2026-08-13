#include "power/PowerHandlerFactory.hpp"

#if defined(__linux__)
#include "linux/LinuxPowerHandler.hpp"
#elif defined(_WIN32)
  #error "Power handler is not implemented on Windows."
#else
  #error "Power handler is not implemented on this platform."
#endif

namespace clipbird::power {

std::unique_ptr<PowerHandler> createPowerHandler(
  std::function<void()> onSleep,
  std::function<void()> onWake
) {
  return std::make_unique<LinuxPowerHandler>(std::move(onSleep), std::move(onWake));
}

}  // namespace clipbird::power
