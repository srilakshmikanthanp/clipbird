#pragma once

#include <cstddef>
#include <cstdint>
#include <memory>
#include <vector>

#include "bluetooth/rfcomm/BluetoothManager.hpp"
#include "io/ChannelFfi.hpp"
#include "io/ServerFfi.hpp"

namespace clipbird::bluetooth::rfcomm {

/**
 * Struct representing a BluetoothManager instance to hold the implementation of the BluetoothManager interface.
 */
struct ClipBirdBluetoothManager {
  std::unique_ptr<BluetoothManager> impl;
};

/**
 * Struct representing a list of Bluetooth devices.
 */
struct ClipBirdBluetoothDeviceList {
  std::vector<BluetoothDevice> devices;
};

}  // namespace clipbird::bluetooth::rfcomm

extern "C" {

/**
 * Retrieves a list of bonded Bluetooth devices.
 *
 * @param manager A pointer to the ClipBirdBluetoothManager instance.
 * @return A pointer to a ClipBirdBluetoothDeviceList containing the bonded devices.
 */
clipbird::bluetooth::rfcomm::ClipBirdBluetoothDeviceList* clipbird_bluetooth_manager_bonded_devices(clipbird::bluetooth::rfcomm::ClipBirdBluetoothManager* manager);

/**
 * Retrieves the number of Bluetooth devices in the list.
 *
 * @param list A pointer to the ClipBirdBluetoothDeviceList instance.
 * @return The number of Bluetooth devices in the list.
 */
std::size_t clipbird_bluetooth_device_list_size(const clipbird::bluetooth::rfcomm::ClipBirdBluetoothDeviceList* list);

/**
 * Retrieves the address of a Bluetooth device in the list.
 *
 * @param list A pointer to the ClipBirdBluetoothDeviceList instance.
 * @param index The index of the Bluetooth device.
 * @return The Bluetooth device address.
 */
const char* clipbird_bluetooth_device_address(const clipbird::bluetooth::rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t index);

/**
 * Retrieves the name of a Bluetooth device in the list.
 *
 * @param list A pointer to the ClipBirdBluetoothDeviceList instance.
 * @param index The index of the Bluetooth device.
 * @return The Bluetooth device name.
 */
const char* clipbird_bluetooth_device_name(const clipbird::bluetooth::rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t index);

/**
 * Retrieves the number of service UUIDs for a Bluetooth device in the list.
 *
 * @param list A pointer to the ClipBirdBluetoothDeviceList instance.
 * @param index The index of the Bluetooth device.
 * @return The number of service UUIDs for the Bluetooth device.
 */
std::size_t clipbird_bluetooth_device_service_uuid_count(const clipbird::bluetooth::rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t index);

/**
 * Retrieves a service UUID for a Bluetooth device in the list.
 *
 * @param list A pointer to the ClipBirdBluetoothDeviceList instance.
 * @param device_index The index of the Bluetooth device.
 * @param uuid_index The index of the service UUID.
 * @return A pointer to the Bluetooth device service UUID bytes. The UUID is 16 bytes long.
 */
const std::uint8_t* clipbird_bluetooth_device_service_uuid(const clipbird::bluetooth::rfcomm::ClipBirdBluetoothDeviceList* list, std::size_t device_index, std::size_t uuid_index);

/**
 * Destroys a Bluetooth device list and releases any associated resources.
 *
 * @param list A pointer to the ClipBirdBluetoothDeviceList instance to destroy.
 */
void clipbird_bluetooth_device_list_destroy(clipbird::bluetooth::rfcomm::ClipBirdBluetoothDeviceList* list);

/**
 * Connects to a Bluetooth device with the specified address and service UUID.
 *
 * @param manager A pointer to the ClipBirdBluetoothManager instance.
 * @param address The Bluetooth address of the device to connect to.
 * @param service_uuid The UUID of the service to connect to.
 * @return A pointer to a ClipBirdChannel representing the connection,
 * or nullptr if the connection could not be established. The caller
 * is responsible for destroying the channel using clipbird_channel_destroy.
 */
clipbird::io::ClipBirdChannel* clipbird_bluetooth_manager_connect(clipbird::bluetooth::rfcomm::ClipBirdBluetoothManager* manager, const char* address, const char* service_uuid);

/**
 * Starts a Bluetooth server with the specified service name and UUID.
 *
 * @param manager A pointer to the ClipBirdBluetoothManager instance.
 * @param service_name The name of the Bluetooth service to create.
 * @param service_uuid The UUID of the Bluetooth service to create.
 * @return A pointer to a ClipBirdServer representing the server, or
 * nullptr if the server could not be started. The caller is responsible
 * for destroying the server using clipbird_server_destroy.
 */
clipbird::io::ClipBirdServer* clipbird_bluetooth_manager_start(clipbird::bluetooth::rfcomm::ClipBirdBluetoothManager* manager, const char* service_name, const char* service_uuid);

/**
 * Destroys the BluetoothManager instance and releases any associated resources.
 *
 * @param manager A pointer to the ClipBirdBluetoothManager instance to destroy.
 */
void clipbird_bluetooth_manager_destroy(clipbird::bluetooth::rfcomm::ClipBirdBluetoothManager* manager);

}
