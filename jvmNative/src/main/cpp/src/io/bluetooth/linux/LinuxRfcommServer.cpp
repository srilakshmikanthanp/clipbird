#include "LinuxRfcommServer.hpp"
#include "LinuxRfcommServerProfile.hpp"

namespace clipbird::io::bluetooth {

LinuxRfcommServer::LinuxRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid)
  : connection(sdbus::createSystemBusConnection()),
    profileRegistration (std::make_unique<LinuxRfcommServerProfile>(*connection, serviceName, serviceUuid)) {
  connection->enterEventLoopAsync();
}

std::unique_ptr<io::Channel> LinuxRfcommServer::accept() {
  return profileRegistration->accept();
}

LinuxRfcommServer::~LinuxRfcommServer() {
	profileRegistration.reset();
	connection->leaveEventLoop();
}

}  // namespace clipbird::io::bluetooth
