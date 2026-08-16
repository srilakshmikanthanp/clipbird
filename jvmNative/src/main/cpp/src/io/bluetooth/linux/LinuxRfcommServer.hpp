#pragma once

#include <memory>
#include <string>

#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

#include "io/Server.hpp"

namespace clipbird::io::bluetooth {

class LinuxRfcommServerProfile;

class LinuxRfcommServer final : public io::Server {
 public:
  LinuxRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid);
  std::unique_ptr<io::Channel> accept() override;
  void close() override;
  ~LinuxRfcommServer() override;

 private:
  std::unique_ptr<sdbus::IConnection> connection;
  std::unique_ptr<LinuxRfcommServerProfile> profileRegistration;
};

}  // namespace clipbird::io::bluetooth
