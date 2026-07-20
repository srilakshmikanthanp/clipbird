#pragma once

#include <cstdint>
#include <optional>

#include <bluetooth/bluetooth.h>
#include <boost/uuid/uuid.hpp>

namespace clipbird::io::bluetooth {

class LinuxSdpResolver final {
 public:
  LinuxSdpResolver() = default;
  LinuxSdpResolver(const LinuxSdpResolver&) = delete;
  LinuxSdpResolver& operator=(const LinuxSdpResolver&) = delete;
  ~LinuxSdpResolver() = default;

  std::optional<uint8_t> rfcommChannel(const bdaddr_t& remote, const boost::uuids::uuid& serviceUuid);
};

}  // namespace clipbird::io::bluetooth
