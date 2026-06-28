#pragma once

#include <memory>
#include <cstdint>

#include "Channel.hpp"

namespace clipbird::io {

/**
 * Enumeration of error codes for channel operations.
 */
enum class ChannelErrorCode: int {
  INVALID_ARGUMENT = 0,
  INTERNAL_ERROR = 1
};

/**
 * Struct representing a Channel instance to hold the implementation of the Channel interface.
 */
struct ClipBirdChannel {
  std::unique_ptr<Channel> impl;
};

}  // namespace clipbird::io

extern "C" {

/**
 * Reads exactly `length` bytes from the specified channel into the provided buffer.
 * @param channel A pointer to the ClipBirdChannel instance to read from.
 * @param buffer A pointer to the buffer where the read data will be stored.
 * @param length The number of bytes to read.
 * @return 0 on success, or a non-zero error code on failure.
 */
bool clipbird_channel_read_exactly(clipbird::io::ClipBirdChannel* channel, std::uint8_t* buffer, std::size_t length);

/**
 * Writes `length` bytes from the provided data buffer to the specified channel.
 * @param channel A pointer to the ClipBirdChannel instance to write to.
 * @param data A pointer to the buffer containing the data to write.
 * @param length The number of bytes to write.
 * @return 0 on success, or a non-zero error code on failure.
 */
bool clipbird_channel_write(clipbird::io::ClipBirdChannel* channel, const std::uint8_t* data, std::size_t length);

/**
 * Destroys a Channel instance and releases its resources.
 * @param channel A pointer to the ClipBirdChannel instance to destroy.
 */
void clipbird_channel_destroy(clipbird::io::ClipBirdChannel* channel);

}
