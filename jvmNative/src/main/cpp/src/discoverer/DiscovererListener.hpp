#pragma once

#include <cstdint>
#include <string>

namespace clipbird {

struct DiscovererListener {
  virtual ~DiscovererListener() = default;
  virtual void onDiscoveryStarted() = 0;
  virtual void onDeviceDiscovered(std::int64_t deviceId) = 0;
  virtual void onDiscoveryFailed(int code, const std::string& reason) = 0;
  virtual void onDiscoveryStopped() = 0;
};

}  // namespace clipbird
