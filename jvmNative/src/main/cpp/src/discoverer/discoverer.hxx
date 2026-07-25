#pragma once

#include <memory>

#include "discoverer/Discoverer.hpp"
#include "discoverer/DiscovererListener.hpp"
#include "discoverer/discoverer.h"

struct clipbird_discoverer {
  std::unique_ptr<clipbird::DiscovererListener> listener;
  std::unique_ptr<clipbird::Discoverer> impl;
};
