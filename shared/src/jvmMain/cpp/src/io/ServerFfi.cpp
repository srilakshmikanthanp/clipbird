#include "ServerFfi.hpp"
#include "error/Error.hpp"
#include "utility/utility.hpp"

namespace error = clipbird::error;
namespace utility = clipbird::utility;
namespace io = clipbird::io;

extern "C" {

io::ClipBirdChannel* clipbird_server_accept(io::ClipBirdServer* server) {
  if (!server) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INVALID_ARGUMENT), "Invalid argument: server must not be null.");
    return nullptr;
  }

  try {
    std::unique_ptr<io::Channel> channel = server->impl->accept();
    error::clearLastError();
    return new io::ClipBirdChannel{std::move(channel)};
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), "INTERNAL_ERROR error occurred while accepting a connection.");
    return nullptr;
  }
}

void clipbird_server_destroy(io::ClipBirdServer* server) {
  delete server;
}

}
