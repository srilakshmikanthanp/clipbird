#pragma once

#include <stddef.h>
#include <stdint.h>

#include "io/bluetooth/bluetooth_manager_factory.h"
#include "io/bluetooth/bluetooth_channel.h"
#include "io/bluetooth/bluetooth_server.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_bluetooth_device_list clipbird_bluetooth_device_list_t;

typedef enum clipbird_bluetooth_manager_error_code {
  CLIPBIRD_BLUETOOTH_MANAGER_INVALID_ARGUMENT = 0,
  CLIPBIRD_BLUETOOTH_MANAGER_INTERNAL_ERROR = 1,
  CLIPBIRD_BLUETOOTH_MANAGER_INVALID_DEVICE_ADDRESS = 2,
  CLIPBIRD_BLUETOOTH_MANAGER_DEVICE_NOT_FOUND = 3,
  CLIPBIRD_BLUETOOTH_MANAGER_SERVICE_NOT_FOUND = 4,
  CLIPBIRD_BLUETOOTH_MANAGER_IO_ERROR = 5
} clipbird_bluetooth_manager_error_code_t;

typedef void (*clipbird_bluetooth_manager_bonded_devices_changed_callback_t)(void* context);

/**
 * Retrieves a list of bonded Bluetooth devices.
 * @param manager A pointer to the Bluetooth manager instance.
 * @return A pointer to a device list containing the bonded devices.
 */
clipbird_bluetooth_device_list_t* clipbird_bluetooth_manager_bonded_devices(clipbird_bluetooth_manager_t* manager);

/**
 * Retrieves the local Bluetooth adapter name.
 * @param manager A pointer to the Bluetooth manager instance.
 * @return The local adapter name, or nullptr on error. The returned string is owned by the manager
 *         and is valid until the next call to this function on the same thread.
 */
const char* clipbird_bluetooth_manager_local_name(clipbird_bluetooth_manager_t* manager);

/**
 * Registers a callback that is invoked when the bonded-device set changes.
 * The callback does not receive device data; call clipbird_bluetooth_manager_bonded_devices() to refresh the latest list.
 * @param manager A pointer to the Bluetooth manager instance.
 * @param callback The callback to invoke when the bonded-device set changes.
 * @param context Opaque user data passed back to the callback.
 */
void clipbird_bluetooth_manager_set_bonded_devices_changed_callback(clipbird_bluetooth_manager_t* manager, clipbird_bluetooth_manager_bonded_devices_changed_callback_t callback, void* context);

/**
 * Removes the bonded-device change callback previously registered with clipbird_bluetooth_manager_set_bonded_devices_changed_callback().
 * @param manager A pointer to the Bluetooth manager instance.
 */
void clipbird_bluetooth_manager_remove_bonded_devices_changed_callback(clipbird_bluetooth_manager_t* manager);

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
clipbird_io_bluetooth_channel_t* clipbird_bluetooth_manager_connect_rfcomm(clipbird_bluetooth_manager_t* manager, const char* address, const char* service_uuid);

/**
 * Starts a Bluetooth RFCOMM server with the specified service name and UUID.
 * @param manager A pointer to the Bluetooth manager instance.
 * @param service_name The name of the RFCOMM service to create.
 * @param service_uuid The UUID of the RFCOMM service to create.
 * @return A pointer to a server representing the RFCOMM server, or nullptr if the server could not be started.
 */
clipbird_io_bluetooth_server_t* clipbird_bluetooth_manager_start_rfcomm_server(clipbird_bluetooth_manager_t* manager, const char* service_name, const char* service_uuid);

/**
 * Destroys the Bluetooth manager instance and releases any associated resources.
 * @param manager A pointer to the Bluetooth manager instance to destroy.
 */
void clipbird_bluetooth_manager_destroy(clipbird_bluetooth_manager_t* manager);

#ifdef __cplusplus
}
#endif
