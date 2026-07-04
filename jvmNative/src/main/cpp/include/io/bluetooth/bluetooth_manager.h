#pragma once

#include <stddef.h>
#include <stdint.h>

#include "io/bluetooth/bluetooth_manager_factory.h"
#include "io/channel.h"
#include "io/server.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_bluetooth_device_list clipbird_bluetooth_device_list_t;

/**
 * Retrieves a list of bonded Bluetooth devices.
 * @param manager A pointer to the Bluetooth manager instance.
 * @return A pointer to a device list containing the bonded devices.
 */
clipbird_bluetooth_device_list_t* clipbird_bluetooth_manager_bonded_devices(clipbird_bluetooth_manager_t* manager);

/**
 * Retrieves the number of Bluetooth devices in the list.
 * @param list A pointer to the device list instance.
 * @return The number of Bluetooth devices in the list.
 */
size_t clipbird_bluetooth_device_list_size(const clipbird_bluetooth_device_list_t* list);

/**
 * Retrieves the address of a Bluetooth device in the list.
 * @param list A pointer to the device list instance.
 * @param index The index of the Bluetooth device.
 * @return The Bluetooth device address.
 */
const char* clipbird_bluetooth_device_address(const clipbird_bluetooth_device_list_t* list, size_t index);

/**
 * Retrieves the name of a Bluetooth device in the list.
 * @param list A pointer to the device list instance.
 * @param index The index of the Bluetooth device.
 * @return The Bluetooth device name.
 */
const char* clipbird_bluetooth_device_name(const clipbird_bluetooth_device_list_t* list, size_t index);

/**
 * Retrieves the number of service UUIDs for a Bluetooth device in the list.
 * @param list A pointer to the device list instance.
 * @param index The index of the Bluetooth device.
 * @return The number of service UUIDs for the Bluetooth device.
 */
size_t clipbird_bluetooth_device_service_uuid_count(const clipbird_bluetooth_device_list_t* list, size_t index);

/**
 * Retrieves a service UUID for a Bluetooth device in the list.
 * @param list A pointer to the device list instance.
 * @param device_index The index of the Bluetooth device.
 * @param uuid_index The index of the service UUID.
 * @return A pointer to the Bluetooth device service UUID bytes. The UUID is 16 bytes long.
 */
const uint8_t* clipbird_bluetooth_device_service_uuid(const clipbird_bluetooth_device_list_t* list, size_t device_index, size_t uuid_index);

/**
 * Destroys a Bluetooth device list and releases any associated resources.
 * @param list A pointer to the device list instance to destroy.
 */
void clipbird_bluetooth_device_list_destroy(clipbird_bluetooth_device_list_t* list);

/**
 * Connects to a Bluetooth device over RFCOMM with the specified address and service UUID.
 * @param manager A pointer to the Bluetooth manager instance.
 * @param address The Bluetooth address of the device to connect to.
 * @param service_uuid The UUID of the RFCOMM service to connect to.
 * @return A pointer to a channel representing the connection, or nullptr if the connection could not be established.
 */
clipbird_io_channel_t* clipbird_bluetooth_manager_connect_rfcomm(clipbird_bluetooth_manager_t* manager, const char* address, const char* service_uuid);

/**
 * Starts a Bluetooth RFCOMM server with the specified service name and UUID.
 * @param manager A pointer to the Bluetooth manager instance.
 * @param service_name The name of the RFCOMM service to create.
 * @param service_uuid The UUID of the RFCOMM service to create.
 * @return A pointer to a server representing the RFCOMM server, or nullptr if the server could not be started.
 */
clipbird_io_server_t* clipbird_bluetooth_manager_start_rfcomm_server(clipbird_bluetooth_manager_t* manager, const char* service_name, const char* service_uuid);

/**
 * Destroys the Bluetooth manager instance and releases any associated resources.
 * @param manager A pointer to the Bluetooth manager instance to destroy.
 */
void clipbird_bluetooth_manager_destroy(clipbird_bluetooth_manager_t* manager);

#ifdef __cplusplus
}
#endif
