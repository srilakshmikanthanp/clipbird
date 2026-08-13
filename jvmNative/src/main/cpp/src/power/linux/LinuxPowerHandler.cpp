#include "LinuxPowerHandler.hpp"

#include <boost/log/trivial.hpp>

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
    BOOST_LOG_TRIVIAL(info) << "PrepareForSleep(true): invoking sleep callback";
    onSleepCallback();
    releaseInhibitLock();
  } else {
    BOOST_LOG_TRIVIAL(info) << "PrepareForSleep(false): invoking wake callback";
    onWakeCallback();
    acquireInhibitLock();
  }
}

bool LinuxPowerHandler::acquireInhibitLock() {
  try {
    BOOST_LOG_TRIVIAL(info) << "Acquiring inhibit lock";
    sdbus::UnixFd fd;
    login1Proxy->callMethod("Inhibit").onInterface(kInterface).withArguments(std::string("sleep"), std::string("Clipbird"), std::string("Preparing for suspend"), std::string("delay")).storeResultsTo(fd);
    inhibitLock = std::move(fd);
    BOOST_LOG_TRIVIAL(info) << "Inhibit lock acquired";
    return true;
  } catch (const std::exception& e) {
    BOOST_LOG_TRIVIAL(warning) << "Failed to acquire inhibit lock: " << e.what();
    return false;
  }
}

void LinuxPowerHandler::releaseInhibitLock() {
  if (inhibitLock.has_value()) {
    inhibitLock.reset();
    BOOST_LOG_TRIVIAL(info) << "Inhibit lock released";
  }
}

LinuxPowerHandler::~LinuxPowerHandler() {
  connection->leaveEventLoop();
  releaseInhibitLock();
}

}  // namespace clipbird::power
