#pragma once

#include <string>

#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

#include "LinuxRfcommClientProfile.hpp"

namespace clipbird::io::bluetooth {

class LinuxRfcommConnector final {
 public:
  LinuxRfcommConnector(const std::string& address, const boost::uuids::uuid& serviceUuid);
  LinuxRfcommConnector(const LinuxRfcommConnector&) = delete;
  LinuxRfcommConnector& operator=(const LinuxRfcommConnector&) = delete;
  ~LinuxRfcommConnector();

  sdbus::UnixFd getFd();

 private:
  static std::string normalize(const std::string& address);
  sdbus::ObjectPath findDevicePath();

 private:
  std::unique_ptr<sdbus::IConnection> connection;
  std::string address;
  boost::uuids::uuid serviceUuid;
  LinuxRfcommClientProfile profile;
  std::atomic_bool connected = false;
};

}  // namespace clipbird::io::bluetooth
