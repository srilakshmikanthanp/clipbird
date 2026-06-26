#include "ServerFfi.hpp"

namespace io = clipbird::io;

extern "C" {

io::ClipBirdChannel* clipbird_server_accept(io::ClipBirdServer* server) {
	// TODO: Add implementation.
}

void clipbird_server_destroy(io::ClipBirdServer* server) {
	// TODO: Add implementation.
}

}
