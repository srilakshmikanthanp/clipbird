#pragma once

#include <string>

namespace com::srilakshmikanthanp::clipbird {

struct AdvertiserListener {
  virtual ~AdvertiserListener() = default;
  virtual void onAdvertisingStarted() = 0;
  virtual void onAdvertisingFailed(int code, const std::string& reason) = 0;
  virtual void onAdvertisingStopped() = 0;
};

}  // namespace com::srilakshmikanthanp::clipbird
