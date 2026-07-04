#include "LinuxRfcommChannel.hpp"
#include "LinuxRfcommServer.hpp"
#include "LinuxRfcommServerProfile.hpp"

namespace clipbird::bluetooth::rfcomm {

LinuxRfcommServer::LinuxRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid)
  : connection(sdbus::createSystemBusConnection()),
    profileRegistration (std::make_unique<LinuxRfcommServerProfile>(*connection, serviceName, serviceUuid)) {
  connection->enterEventLoopAsync();
}

std::unique_ptr<io::Channel> LinuxRfcommServer::accept() {
  sdbus::UnixFd fd = profileRegistration->accept();
  return std::make_unique<LinuxRfcommChannel>(fd.release());
}

LinuxRfcommServer::~LinuxRfcommServer() {
	profileRegistration.reset();
	connection->leaveEventLoop();
}

}  // namespace clipbird::bluetooth::rfcomm
