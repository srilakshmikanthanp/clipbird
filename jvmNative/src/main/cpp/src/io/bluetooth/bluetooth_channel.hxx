#pragma once

#include <memory>

#include "io/bluetooth/BluetoothChannel.hpp"
#include "io/bluetooth/bluetooth_channel.h"

struct clipbird_io_bluetooth_channel {
  std::unique_ptr<com::srilakshmikanthanp::clipbird::io::bluetooth::BluetoothChannel> impl;
};

namespace com::srilakshmikanthanp::clipbird::io::bluetooth {

/**
 * Takes ownership of [channel] and wraps it in a C-API channel handle. Returns
 * nullptr if [channel] is not a Bluetooth channel, in which case ownership stays
 * with the caller's unique_ptr (which frees it on scope exit).
 */
inline clipbird_io_bluetooth_channel* makeBluetoothChannelHandle(std::unique_ptr<io::Channel> channel) {
  auto* bluetoothChannel = dynamic_cast<BluetoothChannel*>(channel.get());

  if (!bluetoothChannel) {
    return nullptr;
  }

  channel.release();
  return new clipbird_io_bluetooth_channel{std::unique_ptr<BluetoothChannel>(bluetoothChannel)};
}

}  // namespace com::srilakshmikanthanp::clipbird::io::bluetooth
