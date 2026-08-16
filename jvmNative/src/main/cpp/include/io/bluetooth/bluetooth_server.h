#pragma once

#include <stdbool.h>

#include "io/bluetooth/bluetooth_channel.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_io_bluetooth_server clipbird_io_bluetooth_server_t;

typedef enum clipbird_io_bluetooth_server_error_code {
  CLIPBIRD_IO_BLUETOOTH_SERVER_INVALID_ARGUMENT = 0,
  CLIPBIRD_IO_BLUETOOTH_SERVER_INTERNAL_ERROR = 1,
  CLIPBIRD_IO_BLUETOOTH_SERVER_IO_ERROR = 2
} clipbird_io_bluetooth_server_error_code_t;

/**
 * Accepts a new connection on the specified server and returns a pointer to a channel instance representing the accepted connection.
 * @param server A pointer to the server instance to accept a connection from.
 * @return A pointer to a channel instance representing the accepted connection, or nullptr if an error occurred. Use the error handling functions to retrieve the error details.
 */
clipbird_io_bluetooth_channel_t* clipbird_io_bluetooth_server_accept(clipbird_io_bluetooth_server_t* server);

/**
 * Stops listening and unblocks a pending accept on the specified server, which then fails with CLIPBIRD_IO_BLUETOOTH_SERVER_IO_ERROR.
 * Safe to call from any thread and more than once. This only signals: the instance stays valid, so the caller must ensure every
 * accept has returned before calling clipbird_io_bluetooth_server_destroy.
 * @param server A pointer to the server instance to close.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_io_bluetooth_server_close(clipbird_io_bluetooth_server_t* server);

/**
 * Destroys a server instance and releases its resources.
 * The caller must ensure no accept is in flight; call clipbird_io_bluetooth_server_close first and wait for it to return.
 * @param server A pointer to the server instance to destroy.
 */
void clipbird_io_bluetooth_server_destroy(clipbird_io_bluetooth_server_t* server);

#ifdef __cplusplus
}
#endif
