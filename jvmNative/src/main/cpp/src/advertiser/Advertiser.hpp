#pragma once

#include "advertiser/AdvertiserListener.hpp"

namespace clipbird {

class Advertiser {
 public:
  explicit Advertiser(AdvertiserListener& listener) : listener(listener) {}
  virtual ~Advertiser() = default;
  virtual void startAdvertising() = 0;
  virtual void stopAdvertising() = 0;

 protected:
  AdvertiserListener& listener;
};

}  // namespace clipbird
