#include "io/server.hxx"

#include "error/Error.hpp"

#include <exception>
#include <memory>

namespace error = clipbird::error;
namespace io = clipbird::io;

extern "C" {

clipbird_io_channel_t* clipbird_io_server_accept(clipbird_io_server_t* server) {
  if (!server || !server->impl) {
    error::setLastError(CLIPBIRD_IO_SERVER_INVALID_ARGUMENT, "Invalid argument: server must not be null.");
    return nullptr;
  }

  try {
    std::unique_ptr<io::Channel> channel = server->impl->accept();
    error::clearLastError();
    return new clipbird_io_channel{std::move(channel)};
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_IO_SERVER_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_IO_SERVER_INTERNAL_ERROR, "INTERNAL_ERROR error occurred while accepting a connection.");
    return nullptr;
  }
}

void clipbird_io_server_destroy(clipbird_io_server_t* server) {
  delete server;
}

}
