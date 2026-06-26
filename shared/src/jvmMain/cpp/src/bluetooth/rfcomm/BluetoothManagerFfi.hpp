#pragma once

#include <memory>
#include <cstdint>

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
 * Struct representing a Bluetooth device with its address, name, and service UUIDs.
 */
struct ClipBirdBluetoothDevice {
  const char* address;
  const char* name;
  const char** serviceUuids;
  long serviceUuidCount;
};

/**
 * Struct representing a list of Bluetooth devices.
 */
struct ClipBirdBluetoothDeviceList {
  ClipBirdBluetoothDevice* devices;
  long count;
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
clipbird::io::ClipBirdServer* clipbird_bluetooth_manager_start_server(clipbird::bluetooth::rfcomm::ClipBirdBluetoothManager* manager, const char* service_name, const char* service_uuid);

/**
 * Destroys the BluetoothManager instance and releases any associated resources.
 *
 * @param manager A pointer to the ClipBirdBluetoothManager instance to destroy.
 */
void clipbird_bluetooth_manager_destroy(clipbird::bluetooth::rfcomm::ClipBirdBluetoothManager* manager);

}
