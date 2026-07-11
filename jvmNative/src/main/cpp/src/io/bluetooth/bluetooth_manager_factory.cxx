#include "io/bluetooth/bluetooth_manager_factory.hxx"

#include "io/bluetooth/BluetoothManagerFactory.hpp"

namespace bluetooth = clipbird::io::bluetooth;

extern "C" {

clipbird_bluetooth_manager_t* clipbird_bluetooth_manager_create() {
  return new clipbird_bluetooth_manager{bluetooth::createBluetoothManager()};
}

}
