#include "discoverer/ble/ble_discoverer_factory.hxx"

#include "discoverer/ble/BleDiscovererFactory.hpp"
#include "error/Error.hpp"

#include <boost/uuid/string_generator.hpp>

#include <exception>
#include <memory>
#include <string>

namespace ble = clipbird::discoverer::ble;
namespace error = clipbird::error;

namespace {

struct CDiscovererListener : clipbird::DiscovererListener {
  clipbird_discoverer_ble_listener_t cbs;

  explicit CDiscovererListener(const clipbird_discoverer_ble_listener_t& cbs) : cbs(cbs) {}

  void onDiscoveryStarted() override {
    if (cbs.on_started) cbs.on_started(cbs.context);
  }

  void onDeviceDiscovered(std::int64_t deviceId) override {
    if (cbs.on_device_discovered) cbs.on_device_discovered(deviceId, cbs.context);
  }

  void onDiscoveryFailed(int code, const std::string& reason) override {
    if (cbs.on_failed) cbs.on_failed(code, reason.c_str(), cbs.context);
  }

  void onDiscoveryStopped() override {
    if (cbs.on_stopped) cbs.on_stopped(cbs.context);
  }
};

}  // namespace

extern "C" {

clipbird_discoverer_t* clipbird_discoverer_ble_create(const char* service_uuid, const clipbird_discoverer_ble_listener_t* listener) {
  if (!service_uuid || !listener) {
    error::setLastError(clipbird_discoverer_ble_factory_error_code::CLIPBIRD_DISCOVERER_BLE_FACTORY_INTERNAL_ERROR, "Invalid argument: service_uuid and events must not be null.");
    return nullptr;
  }

  try {
    auto discovererHandle = std::make_unique<clipbird_discoverer>();
    discovererHandle->listener = std::make_unique<CDiscovererListener>(*listener);
    boost::uuids::string_generator uuidGenerator;
    discovererHandle->impl = ble::createBleDiscoverer(uuidGenerator(service_uuid), *discovererHandle->listener);
    error::clearLastError();
    return discovererHandle.release();
  } catch (const std::exception& e) {
    error::setLastError(clipbird_discoverer_ble_factory_error_code::CLIPBIRD_DISCOVERER_BLE_FACTORY_INTERNAL_ERROR, e.what());
    return nullptr;
  } catch (...) {
    error::setLastError(clipbird_discoverer_ble_factory_error_code::CLIPBIRD_DISCOVERER_BLE_FACTORY_INTERNAL_ERROR, "Unknown error occurred while creating BLE discoverer.");
    return nullptr;
  }
}

}
