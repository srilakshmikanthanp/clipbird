#include "BluetoothManagerFfi.hpp"

namespace rfcomm = clipbird::bluetooth::rfcomm;
namespace io = clipbird::io;

extern "C" {

rfcomm::ClipBirdBluetoothDeviceList* clipbird_bluetooth_manager_bonded_devices(rfcomm::ClipBirdBluetoothManager* manager) {
  return new rfcomm::ClipBirdBluetoothDeviceList{manager->impl->bondedDevices()};
}

std::size_t clipbird_bluetooth_device_list_size(const rfcomm::ClipBirdBluetoothDeviceList* list) {
  return list->devices.size();
}

const char* clipbird_bluetooth_device_address(const rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t index) {
  return list->devices[index].address.c_str();
}

const char* clipbird_bluetooth_device_name(const rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t index) {
  return list->devices[index].name.c_str();
}

std::size_t clipbird_bluetooth_device_service_uuid_count(const rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t index) {
  return list->devices[index].serviceUuids.size();
}

const char* clipbird_bluetooth_device_service_uuid(const rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t device_index, std::size_t uuid_index) {
  return list->devices[device_index].serviceUuids[uuid_index].c_str();
}

void clipbird_bluetooth_device_list_destroy(rfcomm::ClipBirdBluetoothDeviceList* list) {
  delete list;
}

io::ClipBirdChannel* clipbird_bluetooth_manager_connect(rfcomm::ClipBirdBluetoothManager* manager, const char* address, const char* service_uuid) {
  return new io::ClipBirdChannel{manager->impl->connect(std::string(address), std::string(service_uuid))};
}

io::ClipBirdServer* clipbird_bluetooth_manager_start_server(rfcomm::ClipBirdBluetoothManager* manager, const char* service_name, const char* service_uuid) {
  return new io::ClipBirdServer{manager->impl->start(std::string(service_name), std::string(service_uuid))};
}

void clipbird_bluetooth_manager_destroy(rfcomm::ClipBirdBluetoothManager* manager) {
  delete manager;
}

}
