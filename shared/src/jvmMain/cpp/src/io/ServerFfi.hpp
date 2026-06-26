#pragma once

#include <cstdint>
#include <memory>

#include "io/ChannelFfi.hpp"
#include "io/Server.hpp"

namespace clipbird::io {

struct ClipBirdServer {
  std::unique_ptr<Server> impl;
};

}  // namespace clipbird::io

extern "C" {

/**
 * Accepts a new connection on the specified server and returns a pointer to a ClipBirdChannel instance representing the accepted connection.
 * @param server A pointer to the ClipBirdServer instance to accept a connection from.
 * @return A pointer to a ClipBirdChannel instance representing the accepted connection, or nullptr if an error occurred. Use the error handling functions to retrieve the error details.
 */
clipbird::io::ClipBirdChannel* clipbird_server_accept(clipbird::io::ClipBirdServer* server);

/**
 * Destroys a Server instance and releases its resources.
 * @param server A pointer to the ClipBirdServer instance to destroy.
 */
void clipbird_server_destroy(clipbird::io::ClipBirdServer* server);

}
