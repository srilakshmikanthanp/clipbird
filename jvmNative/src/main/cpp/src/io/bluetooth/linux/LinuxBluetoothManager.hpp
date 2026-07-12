#pragma once

#include <cstddef>
#include <condition_variable>
#include <functional>
#include <mutex>
#include <memory>
#include <stdexcept>
#include <string>
#include <vector>

#include "io/bluetooth/BluetoothManager.hpp"
#include "LinuxRfcommServer.hpp"
#include "LinuxRfcommChannel.hpp"

namespace clipbird::io::bluetooth {

class LinuxBluetoothManager final : public BluetoothManager {
 public:
  LinuxBluetoothManager();
  ~LinuxBluetoothManager() override;

  void setBondedDevicesChangedCallback(std::function<void()> callback) override;
  void removeBondedDevicesChangedCallback() override;

  std::vector<bluetooth::BluetoothDevice> bondedDevices() override;
  std::string localName() override;
  std::unique_ptr<io::Channel> connectRfcomm(const std::string& address, const boost::uuids::uuid& serviceUuid) override;
  std::unique_ptr<io::Server> startRfcommServer(const std::string& serviceName, const boost::uuids::uuid& serviceUuid) override;

 private:
  void notifyBondedDevicesChanged();

  std::unique_ptr<sdbus::IConnection> connection = sdbus::createSystemBusConnection();
  std::unique_ptr<sdbus::IProxy> objectManager;

  std::mutex bondedDevicesChangedMutex;
  std::function<void()> bondedDevicesChangedCallback;

  std::condition_variable bondedDevicesChangedCondition;
  std::size_t bondedDevicesChangedInFlightCount = 0;

 private:
  const char* kBluezDeviceInterface = "org.bluez.Device1";
  const char* kBluezAdapterInterface = "org.bluez.Adapter1";
  const char* kObjectManagerInterface = "org.freedesktop.DBus.ObjectManager";
};

}  // namespace clipbird::io::bluetooth
