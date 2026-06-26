#include "ChannelFfi.hpp"
#include "error/Error.hpp"
#include "utility/utility.hpp"

namespace error = clipbird::error;
namespace utility = clipbird::utility;
namespace io = clipbird::io;

extern "C" {

bool clipbird_channel_read_exactly(io::ClipBirdChannel* channel, std::uint8_t* buffer, std::size_t length) {
  if (!channel || !buffer || length <= 0) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INVALID_ARGUMENT), "Invalid argument: channel, buffer must not be null and length must be greater than 0.");
    return false;
  }

  try {
    std::vector<std::uint8_t> data = channel->impl->readExactly(length);
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), e.what());
    return false;
  } catch (...) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), "INTERNAL_ERROR error occurred while reading from the channel.");
    return false;
  }
}

bool clipbird_channel_write(io::ClipBirdChannel* channel, const std::uint8_t* data, std::size_t length) {
  if (!channel || !data || length <= 0) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INVALID_ARGUMENT), "Invalid argument: channel, data must not be null and length must be greater than 0.");
    return false;
  }

  try {
    std::vector<std::uint8_t> buffer(data, data + length);
    channel->impl->write(buffer);
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), e.what());
    return false;
  } catch (...) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), "INTERNAL_ERROR error occurred while writing to the channel.");
    return false;
  }
}

bool clipbird_channel_is_open(const io::ClipBirdChannel* channel, bool* is_open) {
  if (!channel || !is_open) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INVALID_ARGUMENT), "Invalid argument: channel and is_open must not be null.");
    return false;
  }

  try {
    *is_open = channel->impl->isOpen();
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), e.what());
    return false;
  } catch (...) {
    error::setLastError(utility::toUnderlying(io::ChannelErrorCode::INTERNAL_ERROR), "INTERNAL_ERROR error occurred while checking if the channel is open.");
    return false;
  }
}

void clipbird_channel_destroy(io::ClipBirdChannel* channel) {
	delete channel;
}

}
