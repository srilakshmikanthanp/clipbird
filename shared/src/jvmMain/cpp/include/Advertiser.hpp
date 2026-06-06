#pragma once

#include <cstdint>
#include <string>
#include <vector>

namespace clipbird {
class Advertiser {
 public:
  virtual ~Advertiser() = default;
  virtual void startAdvertising() = 0;
  virtual bool isAdvertising() const = 0;
  virtual void stopAdvertising() = 0;
};
}  // namespace clipbird
