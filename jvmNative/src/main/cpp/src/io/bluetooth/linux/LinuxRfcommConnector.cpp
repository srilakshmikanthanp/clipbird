#include "io/IOException.hpp"
#include "io/bluetooth/BluetoothInvalidDeviceAddressException.hpp"
#include "LinuxRfcommConnector.hpp"
#include "LinuxSdpResolver.hpp"

#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>

#include <sys/socket.h>
#include <unistd.h>

namespace clipbird::io::bluetooth {

LinuxRfcommConnector::LinuxRfcommConnector(
  const std::string& address,
  const boost::uuids::uuid& serviceUuid
) : serviceUuid(serviceUuid) {
  if (str2ba(address.c_str(), &remote) != 0) {
    throw BluetoothInvalidDeviceAddressException("Invalid Bluetooth address: " + address);
  }
}

int LinuxRfcommConnector::getFd() {
  auto socket = ::socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

  if (socket < 0) {
    throw io::IOException("Failed to create RFCOMM socket");
  }

  auto resolver = LinuxSdpResolver(remote, serviceUuid);
  auto channel = resolver.rfcommChannel();

  if (!channel) {
    throw io::IOException("No RFCOMM channel found for the requested service");
  }

  struct sockaddr_rc addr = {};
  addr.rc_family = AF_BLUETOOTH;
  addr.rc_bdaddr = remote;
  addr.rc_channel = *channel;

  struct sockaddr *ptr = reinterpret_cast<struct sockaddr*>(&addr);

  if (::connect(socket, ptr, sizeof(addr)) < 0) {
    ::close(socket);
    throw io::IOException("Failed to connect RFCOMM socket");
  }

  return socket;
}

}  // namespace clipbird::io::bluetooth
