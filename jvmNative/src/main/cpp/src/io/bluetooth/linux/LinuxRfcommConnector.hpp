#pragma once

#include <string>

#include <bluetooth/bluetooth.h>
#include <boost/uuid/uuid.hpp>

namespace clipbird::io::bluetooth {

class LinuxRfcommConnector final {
 public:
  LinuxRfcommConnector(const std::string& address, const boost::uuids::uuid& serviceUuid);
  LinuxRfcommConnector(const LinuxRfcommConnector&) = delete;
  LinuxRfcommConnector& operator=(const LinuxRfcommConnector&) = delete;
  ~LinuxRfcommConnector() = default;

  int getFd();

 private:
  bdaddr_t remote;
  boost::uuids::uuid serviceUuid;
};

}  // namespace clipbird::io::bluetooth
