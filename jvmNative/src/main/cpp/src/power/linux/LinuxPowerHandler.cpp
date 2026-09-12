#include "LinuxPowerHandler.hpp"

#include <spdlog/spdlog.h>

#include "utility/utility.hpp"

namespace clipbird::power {

LinuxPowerHandler::LinuxPowerHandler(std::function<void()> onSleep, std::function<void()> onWake)
  : PowerHandler(std::move(onSleep), std::move(onWake)),
    connection(sdbus::createSystemBusConnection()),
    login1Proxy(sdbus::createProxy(*connection, sdbus::ServiceName(kService), sdbus::ObjectPath(kPath))) {

  login1Proxy->uponSignal("PrepareForSleep").onInterface(kInterface).call([this](bool suspending) { onPrepareForSleep(suspending); });
  connection->enterEventLoopAsync();
  acquireInhibitLock();
}

void LinuxPowerHandler::onPrepareForSleep(bool suspending) {
  if (suspending) {
    spdlog::info("PrepareForSleep(true): invoking sleep callback");
    onSleepCallback();
    releaseInhibitLock();
  } else {
    spdlog::info("PrepareForSleep(false): invoking wake callback");
    onWakeCallback();
    acquireInhibitLock();
  }
}

bool LinuxPowerHandler::acquireInhibitLock() {
  try {
    spdlog::info("Acquiring inhibit lock");
    sdbus::UnixFd fd;
    login1Proxy->callMethod("Inhibit").onInterface(kInterface).withArguments(std::string("sleep"), std::string("Clipbird"), std::string("Preparing for suspend"), std::string("delay")).storeResultsTo(fd);
    inhibitLock = std::move(fd);
    spdlog::info("Inhibit lock acquired");
    return true;
  } catch (const std::exception& e) {
    spdlog::warn("Failed to acquire inhibit lock: {}", e.what());
    return false;
  }
}

void LinuxPowerHandler::releaseInhibitLock() {
  if (inhibitLock.has_value()) {
    inhibitLock.reset();
    spdlog::info("Inhibit lock released");
  }
}

LinuxPowerHandler::~LinuxPowerHandler() {
  utility::logOnThrow("Failed to leave power handler event loop", [this] { connection->leaveEventLoop(); });
  utility::logOnThrow("Failed to release inhibit lock", [this] { releaseInhibitLock(); });
}

}  // namespace clipbird::power
