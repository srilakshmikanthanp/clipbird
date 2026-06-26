#include "ChannelFfi.hpp"

namespace io = clipbird::io;

extern "C" {

int clipbird_channel_read_exactly(io::ClipBirdChannel* channel, std::uint8_t* buffer, int length) {
	// TODO: Add implementation.
}

int clipbird_channel_write(io::ClipBirdChannel* channel, const std::uint8_t* data, int length) {
	// TODO: Add implementation.
}

int clipbird_channel_is_open(const io::ClipBirdChannel* channel) {
	// TODO: Add implementation.
}

void clipbird_channel_destroy(io::ClipBirdChannel* channel) {
	// TODO: Add implementation.
}

}
