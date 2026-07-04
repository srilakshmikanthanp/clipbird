#pragma once

#include <atomic>
#include <future>
#include <memory>

#include <boost/uuid/uuid.hpp>
#include <sdbus-c++/sdbus-c++.h>

namespace clipbird::bluetooth::rfcomm {

class LinuxRfcommClientProfile final {
 public:
  LinuxRfcommClientProfile(sdbus::IConnection& connection, const boost::uuids::uuid& serviceUuid);
  LinuxRfcommClientProfile(const LinuxRfcommClientProfile&) = delete;
  LinuxRfcommClientProfile& operator=(const LinuxRfcommClientProfile&) = delete;
  ~LinuxRfcommClientProfile();

  sdbus::UnixFd connect(const sdbus::ObjectPath& devicePath);

 private:
  void onNewConnection(const sdbus::ObjectPath& device, sdbus::UnixFd fd, const std::map<std::string, sdbus::Variant>& properties);
  void release();

 private:
  static inline std::atomic_uint64_t nextId{0};
  static std::string nextProfileObjectPath();

 private:
  sdbus::IConnection& connection;
  boost::uuids::uuid serviceUuid;
  std::promise<sdbus::UnixFd> connectionPromise;
  std::future<sdbus::UnixFd> connectionFuture;
  sdbus::ObjectPath objectPath;
  std::unique_ptr<sdbus::IObject> object;
  std::unique_ptr<sdbus::IProxy> profileManager;
  bool registered = false;
};

}  // namespace clipbird::bluetooth::rfcomm
