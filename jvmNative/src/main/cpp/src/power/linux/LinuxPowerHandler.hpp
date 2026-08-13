#pragma once

#include <sdbus-c++/sdbus-c++.h>

#include <memory>
#include <optional>

#include "power/PowerHandler.hpp"

namespace clipbird::power {

class LinuxPowerHandler final : public PowerHandler {
 public:
  LinuxPowerHandler(std::function<void()> onSleep, std::function<void()> onWake);
  ~LinuxPowerHandler() override;

 private:
  void onPrepareForSleep(bool suspending);
  bool acquireInhibitLock();
  void releaseInhibitLock();

  std::unique_ptr<sdbus::IConnection> connection;
  std::unique_ptr<sdbus::IProxy> login1Proxy;
  std::optional<sdbus::UnixFd> inhibitLock;

  static constexpr const char* kService = "org.freedesktop.login1";
  static constexpr const char* kPath = "/org/freedesktop/login1";
  static constexpr const char* kInterface = "org.freedesktop.login1.Manager";
};

}  // namespace clipbird::power
