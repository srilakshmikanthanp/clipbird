#pragma once

#include <memory>

#include "advertiser/Advertiser.hpp"
#include "advertiser/AdvertiserListener.hpp"
#include "advertiser/advertiser.h"

struct clipbird_advertiser {
  std::unique_ptr<clipbird::AdvertiserListener> listener;
  std::unique_ptr<clipbird::Advertiser> impl;
};
