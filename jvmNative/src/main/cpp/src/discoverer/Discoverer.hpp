#pragma once

#include "discoverer/DiscovererListener.hpp"

namespace clipbird {

class Discoverer {
 public:
  explicit Discoverer(DiscovererListener& listener) : listener(listener) {}
  virtual ~Discoverer() = default;
  virtual void startDiscovery() = 0;
  virtual void stopDiscovery() = 0;

 protected:
  DiscovererListener& listener;
};

}  // namespace clipbird