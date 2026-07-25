#pragma once

#include <memory>

#include <boost/uuid/uuid.hpp>

#include "discoverer/Discoverer.hpp"
#include "discoverer/DiscovererListener.hpp"

namespace clipbird::discoverer::ble {

std::unique_ptr<Discoverer> createBleDiscoverer(
  const boost::uuids::uuid& serviceUuid,
  DiscovererListener& listener
);

}  // namespace clipbird::discoverer::ble
