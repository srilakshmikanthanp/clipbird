#include "LinuxRfcommServer.hpp"
#include "LinuxRfcommServerProfile.hpp"
#include "utility/utility.hpp"

namespace clipbird::io::bluetooth {

LinuxRfcommServer::LinuxRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid)
  : connection(sdbus::createSystemBusConnection()) {
  connection->enterEventLoopAsync();
  profileRegistration = std::make_unique<LinuxRfcommServerProfile>(*connection, serviceName, serviceUuid);
}

std::unique_ptr<io::Channel> LinuxRfcommServer::accept() {
  return profileRegistration->accept();
}

void LinuxRfcommServer::close() {
  if (profileRegistration) {
    profileRegistration->close();
  }
}

LinuxRfcommServer::~LinuxRfcommServer() {
	utility::logOnThrow("Failed to leave RFCOMM server event loop", [this] { connection->leaveEventLoop(); });
	utility::logOnThrow("Failed to release RFCOMM server profile", [this] { profileRegistration.reset(); });
}

}  // namespace clipbird::io::bluetooth
