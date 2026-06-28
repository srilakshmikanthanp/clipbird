#pragma once

#include <memory>
#include <string>

#include <boost/thread/sync_queue.hpp>
#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

namespace clipbird::bluetooth::rfcomm {

class LinuxRfcommServerProfile final {
 public:
  LinuxRfcommServerProfile(sdbus::IConnection& connection, const std::string& serviceName, const boost::uuids::uuid& serviceUuid);
  LinuxRfcommServerProfile(const LinuxRfcommServerProfile&) = delete;
  LinuxRfcommServerProfile& operator=(const LinuxRfcommServerProfile&) = delete;
  ~LinuxRfcommServerProfile();

  sdbus::UnixFd accept();

 private:
  void release();
  void onNewConnection(const sdbus::ObjectPath&, sdbus::UnixFd fd, const std::map<std::string, sdbus::Variant>& properties);

  boost::sync_queue<sdbus::UnixFd> acceptedConnections;
  sdbus::ObjectPath objectPath;
  std::unique_ptr<sdbus::IObject> object;
  std::unique_ptr<sdbus::IProxy> profileManager;
  bool registered = false;
};

}  // namespace clipbird::bluetooth::rfcomm
