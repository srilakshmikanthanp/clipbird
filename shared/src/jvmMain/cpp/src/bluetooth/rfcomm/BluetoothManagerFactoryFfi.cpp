#include "BluetoothManagerFactoryFfi.hpp"

#include "BluetoothManagerFactory.hpp"

namespace rfcomm = clipbird::bluetooth::rfcomm;

extern "C" {

rfcomm::ClipBirdBluetoothManager* clipbird_bluetooth_manager_create() {
  return new rfcomm::ClipBirdBluetoothManager{rfcomm::createBluetoothManager()};
}

}
