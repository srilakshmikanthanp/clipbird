#include "BluetoothManagerFfi.hpp"

namespace rfcomm = clipbird::bluetooth::rfcomm;
namespace io = clipbird::io;

extern "C" {

rfcomm::ClipBirdBluetoothDeviceList* clipbird_bluetooth_manager_bonded_devices(rfcomm::ClipBirdBluetoothManager* manager) {
	// TODO: Add implementation.
}

io::ClipBirdChannel* clipbird_bluetooth_manager_connect(rfcomm::ClipBirdBluetoothManager* manager, const char* address, const char* service_uuid) {
	// TODO: Add implementation.
}

io::ClipBirdServer* clipbird_bluetooth_manager_start_server(rfcomm::ClipBirdBluetoothManager* manager, const char* service_name, const char* service_uuid) {
	// TODO: Add implementation.
}

void clipbird_bluetooth_manager_destroy(rfcomm::ClipBirdBluetoothManager* manager) {
	// TODO: Add implementation.
}

}
