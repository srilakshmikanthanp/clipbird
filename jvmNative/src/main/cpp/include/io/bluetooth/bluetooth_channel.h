#pragma once

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_io_bluetooth_channel clipbird_io_bluetooth_channel_t;

typedef enum clipbird_io_bluetooth_channel_error_code {
  CLIPBIRD_IO_BLUETOOTH_CHANNEL_INVALID_ARGUMENT = 0,
  CLIPBIRD_IO_BLUETOOTH_CHANNEL_INTERNAL_ERROR = 1,
  CLIPBIRD_IO_BLUETOOTH_CHANNEL_EOF = 2,
  CLIPBIRD_IO_BLUETOOTH_CHANNEL_IO_ERROR = 3
} clipbird_io_bluetooth_channel_error_code_t;

/**
 * Reads exactly `length` bytes from the specified channel into the provided buffer.
 * @param channel A pointer to the channel instance to read from.
 * @param buffer A pointer to the buffer where the read data will be stored.
 * @param length The number of bytes to read.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_io_bluetooth_channel_read_exactly(clipbird_io_bluetooth_channel_t* channel, uint8_t* buffer, size_t length);

/**
 * Writes `length` bytes from the provided data buffer to the specified channel.
 * @param channel A pointer to the channel instance to write to.
 * @param data A pointer to the buffer containing the data to write.
 * @param length The number of bytes to write.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_io_bluetooth_channel_write(clipbird_io_bluetooth_channel_t* channel, const uint8_t* data, size_t length);

/**
 * Retrieves the Bluetooth address of the remote peer for the specified channel.
 * @param channel A pointer to the channel instance.
 * @return A null-terminated string with the remote address, or nullptr if the
 * channel is null. The returned pointer is owned by the library and remains
 * valid until the next call on the same thread.
 */
const char* clipbird_io_bluetooth_channel_remote_address(clipbird_io_bluetooth_channel_t* channel);

/**
 * Destroys a channel instance and releases its resources.
 * @param channel A pointer to the channel instance to destroy.
 */
void clipbird_io_bluetooth_channel_destroy(clipbird_io_bluetooth_channel_t* channel);

#ifdef __cplusplus
}
#endif
