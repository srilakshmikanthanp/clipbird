#include "LinuxRfcommChannel.hpp"

#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include <sys/socket.h>
#include <unistd.h>

#include <stdexcept>
#include <system_error>

#include "io/EOFException.hpp"

namespace clipbird::bluetooth {

LinuxRfcommChannel::LinuxRfcommChannel(int socket_fd) : socket_fd(socket_fd) {
  // No additional initialization required for now.
}

std::vector<std::uint8_t> LinuxRfcommChannel::readExactly(std::size_t size) {
  std::vector<std::uint8_t> buffer(size);
  std::size_t totalRead = 0;

  while (totalRead < size) {
    ssize_t bytesRead = ::read(socket_fd, buffer.data() + totalRead, size - totalRead);

    if (bytesRead < 0) {
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

LinuxRfcommChannel::~LinuxRfcommChannel() {
  ::close(socket_fd);
}

}  // namespace clipbird::bluetooth
