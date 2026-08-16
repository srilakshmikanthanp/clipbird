#include "LinuxRfcommServer.hpp"
#include "LinuxRfcommServerProfile.hpp"

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
	connection->leaveEventLoop();
	profileRegistration.reset();
}

}  // namespace clipbird::io::bluetooth
