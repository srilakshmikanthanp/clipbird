#include "io/bluetooth/bluetooth_channel.hxx"

#include "error/Error.hpp"
#include "io/EOFException.hpp"
#include "io/IOException.hpp"
#include "io/bluetooth/BluetoothChannel.hpp"

#include <algorithm>
#include <cstdint>
#include <exception>
#include <string>
#include <system_error>
#include <vector>

namespace error = clipbird::error;
namespace io = clipbird::io;

extern "C" {

bool clipbird_io_bluetooth_channel_read_exactly(clipbird_io_bluetooth_channel_t* channel, std::uint8_t* buffer, std::size_t length) {
  if (!channel || !channel->impl || !buffer || length == 0) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INVALID_ARGUMENT, "Invalid argument: channel, buffer must not be null and length must be greater than 0.");
    return false;
  }

  try {
    std::vector<std::uint8_t> data = channel->impl->readExactly(length);
    std::copy(data.begin(), data.end(), buffer);
    error::clearLastError();
    return true;
  } catch (const io::EOFException& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_EOF, e.what());
    return false;
  } catch (const io::IOException& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_IO_ERROR, e.what());
    return false;
  } catch (const std::system_error& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_IO_ERROR, e.what());
    return false;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INTERNAL_ERROR, "Unknown error occurred while reading from the channel.");
    return false;
  }
}

bool clipbird_io_bluetooth_channel_write(clipbird_io_bluetooth_channel_t* channel, const std::uint8_t* data, std::size_t length) {
  if (!channel || !channel->impl || !data || length == 0) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INVALID_ARGUMENT, "Invalid argument: channel, data must not be null and length must be greater than 0.");
    return false;
  }

  try {
    std::vector<std::uint8_t> buffer(data, data + length);
    channel->impl->write(buffer);
    error::clearLastError();
    return true;
  } catch (const io::IOException& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_IO_ERROR, e.what());
    return false;
  } catch (const std::system_error& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_IO_ERROR, e.what());
    return false;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INTERNAL_ERROR, "Unknown error occurred while writing to the channel.");
    return false;
  }
}

const char* clipbird_io_bluetooth_channel_remote_address(clipbird_io_bluetooth_channel_t* channel) {
  if (!channel || !channel->impl) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INVALID_ARGUMENT, "Invalid argument: channel must not be null.");
    return nullptr;
  }

  try {
    const char * address =  channel->impl->remoteAddress().c_str();
    error::clearLastError();
    return address;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_CHANNEL_INTERNAL_ERROR, "Unknown error occurred while retrieving the remote address.");
    return nullptr;
  }
}

void clipbird_io_bluetooth_channel_destroy(clipbird_io_bluetooth_channel_t* channel) {
  delete channel;
}

}
