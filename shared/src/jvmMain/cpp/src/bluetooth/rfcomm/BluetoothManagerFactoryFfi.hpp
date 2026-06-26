#pragma once

#include <memory>

#include "BluetoothManagerFfi.hpp"

extern "C" {

/**
 * Creates a new instance of the BluetoothManager.
 *
 * @return A pointer to the newly created ClipBirdBluetoothManager instance.
 */
clipbird::bluetooth::rfcomm::ClipBirdBluetoothManager* clipbird_bluetooth_manager_create();

}
