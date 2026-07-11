#include "io/server.hxx"

#include "error/Error.hpp"
#include "io/IOException.hpp"

#include <exception>
#include <memory>
#include <system_error>

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
  } catch (const io::IOException& e) {
    error::setLastError(CLIPBIRD_IO_SERVER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::system_error& e) {
    error::setLastError(CLIPBIRD_IO_SERVER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_IO_SERVER_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_IO_SERVER_INTERNAL_ERROR, "Unknown error occurred while accepting a connection.");
    return nullptr;
  }
}

void clipbird_io_server_destroy(clipbird_io_server_t* server) {
  delete server;
}

}
