#include "io/bluetooth/bluetooth_server.hxx"

#include "error/Error.hpp"
#include "io/IOException.hpp"

#include <exception>
#include <memory>
#include <system_error>

namespace error = clipbird::error;
namespace io = clipbird::io;

extern "C" {

clipbird_io_bluetooth_channel_t* clipbird_io_bluetooth_server_accept(clipbird_io_bluetooth_server_t* server) {
  if (!server || !server->impl) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_SERVER_INVALID_ARGUMENT, "Invalid argument: server must not be null.");
    return nullptr;
  }

  try {
    auto* channel = io::bluetooth::makeBluetoothChannelHandle(server->impl->accept());

    if (!channel) {
      error::setLastError(CLIPBIRD_IO_BLUETOOTH_SERVER_INTERNAL_ERROR, "Accepted connection is not a Bluetooth channel.");
      return nullptr;
    }

    error::clearLastError();
    return channel;
  } catch (const io::IOException& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_SERVER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::system_error& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_SERVER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_SERVER_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_IO_BLUETOOTH_SERVER_INTERNAL_ERROR, "Unknown error occurred while accepting a connection.");
    return nullptr;
  }
}

void clipbird_io_bluetooth_server_destroy(clipbird_io_bluetooth_server_t* server) {
  delete server;
}

}
