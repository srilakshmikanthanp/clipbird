#pragma once

#include "io/channel.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_io_server clipbird_io_server_t;

typedef enum clipbird_io_server_error_code {
  CLIPBIRD_IO_SERVER_INVALID_ARGUMENT = 0,
  CLIPBIRD_IO_SERVER_INTERNAL_ERROR = 1
} clipbird_io_server_error_code_t;

/**
 * Accepts a new connection on the specified server and returns a pointer to a channel instance representing the accepted connection.
 * @param server A pointer to the server instance to accept a connection from.
 * @return A pointer to a channel instance representing the accepted connection, or nullptr if an error occurred. Use the error handling functions to retrieve the error details.
 */
clipbird_io_channel_t* clipbird_io_server_accept(clipbird_io_server_t* server);

/**
 * Destroys a server instance and releases its resources.
 * @param server A pointer to the server instance to destroy.
 */
void clipbird_io_server_destroy(clipbird_io_server_t* server);

#ifdef __cplusplus
}
#endif
