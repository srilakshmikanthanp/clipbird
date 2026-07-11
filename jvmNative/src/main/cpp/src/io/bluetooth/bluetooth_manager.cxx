#include "io/bluetooth/bluetooth_manager.hxx"

#include "error/Error.hpp"
#include "io/bluetooth/BluetoothDeviceNotFoundException.hpp"
#include "io/bluetooth/BluetoothInvalidDeviceAddressException.hpp"
#include "io/bluetooth/BluetoothServiceNotFoundException.hpp"
#include "io/IOException.hpp"

#include <boost/uuid/string_generator.hpp>

#include <cstddef>
#include <cstdint>
#include <exception>
#include <stdexcept>
#include <string>
#include <system_error>

namespace error = clipbird::error;
namespace bt = clipbird::io::bluetooth;
namespace io = clipbird::io;

extern "C" {

clipbird_bluetooth_device_list_t* clipbird_bluetooth_manager_bonded_devices(clipbird_bluetooth_manager_t* manager) {
  if (!manager || !manager->impl) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INVALID_ARGUMENT, "Invalid argument: manager must not be null.");
    return nullptr;
  }

  try {
    auto devices = manager->impl->bondedDevices();
    error::clearLastError();
    return new clipbird_bluetooth_device_list{std::move(devices)};
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INTERNAL_ERROR, "Unknown error occurred while retrieving bonded devices.");
    return nullptr;
  }
}

void clipbird_bluetooth_manager_set_bonded_devices_changed_callback(
  clipbird_bluetooth_manager_t* manager,
  clipbird_bluetooth_manager_bonded_devices_changed_callback_t callback,
  void* context
) {
  if (!manager || !manager->impl) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INVALID_ARGUMENT, "Invalid argument: manager must not be null.");
    return;
  }

  if (!callback) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INVALID_ARGUMENT, "Invalid argument: callback must not be null.");
    return;
  }

  manager->impl->setBondedDevicesChangedCallback([callback, context]() { callback(context); });
  error::clearLastError();
}

void clipbird_bluetooth_manager_remove_bonded_devices_changed_callback(
  clipbird_bluetooth_manager_t* manager
) {
  if (!manager || !manager->impl) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INVALID_ARGUMENT, "Invalid argument: manager must not be null.");
    return;
  } else {
    manager->impl->removeBondedDevicesChangedCallback();
    error::clearLastError();
  }
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

void clipbird_bluetooth_device_list_destroy(clipbird_bluetooth_device_list_t* list) {
  delete list;
}

clipbird_io_channel_t* clipbird_bluetooth_manager_connect_rfcomm(clipbird_bluetooth_manager_t* manager, const char* address, const char* service_uuid) {
  if (!manager || !manager->impl || !address || !service_uuid) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INVALID_ARGUMENT, "Invalid argument: manager, address and service_uuid must not be null.");
    return nullptr;
  }

  try {
    boost::uuids::string_generator uuidGenerator;
    auto channel = manager->impl->connectRfcomm(std::string(address), uuidGenerator(service_uuid));
    error::clearLastError();
    return new clipbird_io_channel{std::move(channel)};
  } catch (const bt::BluetoothDeviceNotFoundException& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_DEVICE_NOT_FOUND, e.what());
    return nullptr;
  } catch (const bt::BluetoothInvalidDeviceAddressException& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INVALID_DEVICE_ADDRESS, e.what());
    return nullptr;
  } catch (const bt::BluetoothServiceNotFoundException& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_SERVICE_NOT_FOUND, e.what());
    return nullptr;
  } catch (const io::IOException& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::system_error& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INTERNAL_ERROR, "Unknown error occurred while connecting RFCOMM.");
    return nullptr;
  }
}

clipbird_io_server_t* clipbird_bluetooth_manager_start_rfcomm_server(clipbird_bluetooth_manager_t* manager, const char* service_name, const char* service_uuid) {
  if (!manager || !manager->impl || !service_name || !service_uuid) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INVALID_ARGUMENT, "Invalid argument: manager, service_name and service_uuid must not be null.");
    return nullptr;
  }

  try {
    boost::uuids::string_generator uuidGenerator;
    auto server = manager->impl->startRfcommServer(std::string(service_name), uuidGenerator(service_uuid));
    error::clearLastError();
    return new clipbird_io_server{std::move(server)};
  } catch (const io::IOException& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::system_error& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_IO_ERROR, e.what());
    return nullptr;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(CLIPBIRD_BLUETOOTH_MANAGER_INTERNAL_ERROR, "Unknown error occurred while starting RFCOMM server.");
    return nullptr;
  }
}

void clipbird_bluetooth_manager_destroy(clipbird_bluetooth_manager_t* manager) {
  delete manager;
}

}
