#include "WindowsRfcommChannel.hpp"

#include <stdexcept>

namespace clipbird::bluetooth {

WindowsRfcommChannel::WindowsRfcommChannel() {
  throw std::runtime_error("WindowsRfcommChannel::WindowsRfcommChannel() is not implemented yet.");
}

std::vector<std::uint8_t> WindowsRfcommChannel::readExactly(std::size_t size) {
	throw std::runtime_error("WindowsRfcommChannel::readExactly() is not implemented yet.");
}

void WindowsRfcommChannel::write(const std::vector<std::uint8_t>& data) {
	throw std::runtime_error("WindowsRfcommChannel::write() is not implemented yet.");
}

WindowsRfcommChannel::~WindowsRfcommChannel() {
  throw std::runtime_error("WindowsRfcommChannel::~WindowsRfcommChannel() is not implemented yet.");
}
}  // namespace clipbird::bluetooth
