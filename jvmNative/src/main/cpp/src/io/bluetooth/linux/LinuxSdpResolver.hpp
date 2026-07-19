#pragma once

#include <cstdint>
#include <optional>

#include <bluetooth/bluetooth.h>
#include <boost/uuid/uuid.hpp>

namespace clipbird::io::bluetooth {

class LinuxSdpResolver final {
 public:
  LinuxSdpResolver(const bdaddr_t& remote, const boost::uuids::uuid& serviceUuid);
  LinuxSdpResolver(const LinuxSdpResolver&) = delete;
  LinuxSdpResolver& operator=(const LinuxSdpResolver&) = delete;
  ~LinuxSdpResolver() = default;

  std::optional<uint8_t> rfcommChannel();

 private:
  bdaddr_t remote;
  boost::uuids::uuid serviceUuid;
};

}  // namespace clipbird::io::bluetooth
