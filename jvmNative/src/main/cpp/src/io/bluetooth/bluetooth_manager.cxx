#include "io/bluetooth/bluetooth_manager.hxx"

#include <boost/uuid/string_generator.hpp>

#include <cstddef>
#include <cstdint>
#include <string>

extern "C" {

clipbird_bluetooth_device_list_t* clipbird_bluetooth_manager_bonded_devices(clipbird_bluetooth_manager_t* manager) {
  return new clipbird_bluetooth_device_list{manager->impl->bondedDevices()};
}

std::size_t clipbird_bluetooth_device_list_size(const clipbird_bluetooth_device_list_t* list) {
  return list->devices.size();
}

const char* clipbird_bluetooth_device_address(const clipbird_bluetooth_device_list_t* list, std::size_t index) {
  return list->devices[index].address.c_str();
}

const char* clipbird_bluetooth_device_name(const clipbird_bluetooth_device_list_t* list, std::size_t index) {
  return list->devices[index].name.c_str();
}

std::size_t clipbird_bluetooth_device_service_uuid_count(const clipbird_bluetooth_device_list_t* list, std::size_t index) {
  return list->devices[index].serviceUuids.size();
}

const std::uint8_t* clipbird_bluetooth_device_service_uuid(const clipbird_bluetooth_device_list_t* list, std::size_t device_index, std::size_t uuid_index) {
  return list->devices[device_index].serviceUuids[uuid_index].data;
}

void clipbird_bluetooth_device_list_destroy(clipbird_bluetooth_device_list_t* list) {
  delete list;
}

clipbird_io_channel_t* clipbird_bluetooth_manager_connect_rfcomm(clipbird_bluetooth_manager_t* manager, const char* address, const char* service_uuid) {
  boost::uuids::string_generator uuidGenerator;
  return new clipbird_io_channel{manager->impl->connectRfcomm(std::string(address), uuidGenerator(service_uuid))};
}

clipbird_io_server_t* clipbird_bluetooth_manager_start_rfcomm_server(clipbird_bluetooth_manager_t* manager, const char* service_name, const char* service_uuid) {
  boost::uuids::string_generator uuidGenerator;
  return new clipbird_io_server{manager->impl->startRfcommServer(std::string(service_name), uuidGenerator(service_uuid))};
}

void clipbird_bluetooth_manager_destroy(clipbird_bluetooth_manager_t* manager) {
  delete manager;
}

}
