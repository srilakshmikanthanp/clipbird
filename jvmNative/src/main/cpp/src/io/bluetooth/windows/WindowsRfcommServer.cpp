#include "WindowsRfcommServer.hpp"

#include <stdexcept>

namespace clipbird::io::bluetooth {

WindowsRfcommServer::WindowsRfcommServer() {
	throw std::runtime_error("WindowsRfcommServer is not implemented yet.");
}

std::unique_ptr<io::Channel> WindowsRfcommServer::accept() {
	throw std::runtime_error("WindowsRfcommServer is not implemented yet.");
}

void WindowsRfcommServer::close() {
	throw std::runtime_error("WindowsRfcommServer is not implemented yet.");
}

}  // namespace clipbird::io::bluetooth
