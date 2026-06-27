#include "WindowsRfcommChannel.hpp"

#include <stdexcept>

namespace clipbird::bluetooth::rfcomm {

WindowsRfcommChannel::WindowsRfcommChannel() {
  throw std::runtime_error("WindowsRfcommChannel::WindowsRfcommChannel() is not implemented yet.");
}

std::vector<std::uint8_t> WindowsRfcommChannel::readExactly(std::size_t size) {
	throw std::runtime_error("WindowsRfcommChannel::readExactly() is not implemented yet.");
}

void WindowsRfcommChannel::write(const std::vector<std::uint8_t>& data) {
	throw std::runtime_error("WindowsRfcommChannel::write() is not implemented yet.");
}

bool WindowsRfcommChannel::isOpen() const {
	throw std::runtime_error("WindowsRfcommChannel::isOpen() is not implemented yet.");
}

void WindowsRfcommChannel::close() {
	throw std::runtime_error("WindowsRfcommChannel::close() is not implemented yet.");
}

}  // namespace clipbird::bluetooth::rfcomm
