#pragma once

#include <memory>

#include "advertiser/Advertiser.hpp"
#include "advertiser/advertiser.h"

struct clipbird_ble_advertiser {
  std::unique_ptr<clipbird::Advertiser> impl;
};
