#include "LinuxRfcommChannel.hpp"

#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include <sys/socket.h>
#include <unistd.h>

#include <boost/log/trivial.hpp>
#include <cerrno>
#include <cstring>
#include <stdexcept>
#include <system_error>
#include <utility>

#include "io/EOFException.hpp"
#include "utility/utility.hpp"

namespace clipbird::io::bluetooth {

LinuxRfcommChannel::LinuxRfcommChannel(int socket_fd, std::string remoteAddress)
  : socket_fd(socket_fd), remote_address(std::move(remoteAddress)) {
  // No additional initialization required for now.
}

const std::string& LinuxRfcommChannel::remoteAddress() const {
  return remote_address;
}

std::vector<std::uint8_t> LinuxRfcommChannel::readExactly(std::size_t size) {
  std::vector<std::uint8_t> buffer(size);
  std::size_t totalRead = 0;

  while (totalRead < size) {
    ssize_t bytesRead = ::read(socket_fd, buffer.data() + totalRead, size - totalRead);

    if (bytesRead < 0) {
      if (errno == EINTR) continue;
      throw std::system_error(errno, std::generic_category(), "Failed to read from RFCOMM socket");
    }

    if (bytesRead == 0) {
      throw io::EOFException("End of stream reached while reading from RFCOMM socket");
    }

    totalRead += static_cast<std::size_t>(bytesRead);
  }

  return buffer;
}

void LinuxRfcommChannel::write(const std::vector<std::uint8_t>& data) {
  std::size_t totalWritten = 0;
  std::size_t size = data.size();

  while (totalWritten < size) {
    ssize_t bytesWritten = ::write(socket_fd, data.data() + totalWritten, size - totalWritten);

    if (bytesWritten < 0) {
      if (errno != EINTR) {
        throw std::system_error(errno, std::generic_category(), "Failed to write to RFCOMM socket");
      } else {
        continue;
      }
    }

    if (bytesWritten == 0) {
      throw std::runtime_error("write() returned 0");
    }

    totalWritten += static_cast<std::size_t>(bytesWritten);
  }
}

void LinuxRfcommChannel::close() {
  if (shutdown_done.exchange(true)) {
    return;
  }

  if (::shutdown(socket_fd, SHUT_RDWR) == 0) {
    return;
  }

  if (errno == ENOTCONN) {
    return;
  }

  throw std::system_error(errno, std::generic_category(), "Failed to shut down RFCOMM socket");
}

LinuxRfcommChannel::~LinuxRfcommChannel() {
  utility::logOnThrow("Failed to close RFCOMM socket", [this] {
    if (::close(socket_fd) != 0) throw std::system_error(errno, std::generic_category(), "close");
  });
}

}  // namespace clipbird::io::bluetooth
