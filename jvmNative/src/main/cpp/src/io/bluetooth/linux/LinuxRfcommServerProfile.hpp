#pragma once

#include <memory>
#include <string>
#include <tuple>

#include <boost/thread/sync_queue.hpp>
#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

#include "io/Channel.hpp"

namespace clipbird::io::bluetooth {

class LinuxRfcommServerProfile final {
 public:
  LinuxRfcommServerProfile(sdbus::IConnection& connection, const std::string& serviceName, const boost::uuids::uuid& serviceUuid);
  LinuxRfcommServerProfile(const LinuxRfcommServerProfile&) = delete;
  LinuxRfcommServerProfile& operator=(const LinuxRfcommServerProfile&) = delete;
  ~LinuxRfcommServerProfile();

  std::unique_ptr<io::Channel> accept();

 private:
  void release();
  void onNewConnection(const sdbus::ObjectPath& device, sdbus::UnixFd fd, const std::map<std::string, sdbus::Variant>& properties);

  sdbus::IConnection& connection;
  boost::sync_queue<std::tuple<sdbus::UnixFd, std::string>> acceptedConnections;
  sdbus::ObjectPath objectPath;
  std::unique_ptr<sdbus::IObject> object;
  std::unique_ptr<sdbus::IProxy> profileManager;
  bool registered = false;
};

}  // namespace clipbird::io::bluetooth
