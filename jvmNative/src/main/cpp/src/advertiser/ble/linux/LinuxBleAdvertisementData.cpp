#include "LinuxBleAdvertisementData.hpp"

#include <map>

namespace clipbird::advertiser::ble {
LinuxBleAdvertisementData::LinuxBleAdvertisementData(sdbus::IConnection& connection,
                                                     sdbus::ObjectPath objectPath,
                                                     boost::uuids::uuid serviceUuid,
                                                     std::vector<std::uint8_t> serviceData,
                                                     std::function<void()> onRelease)
: object(sdbus::createObject(connection, objectPath)),
  objectPath(objectPath),
  serviceUuid(serviceUuid),
  serviceData(serviceData),
  onRelease(onRelease) {

  const auto manufacturerDataGetter = [this]() {
    std::vector<std::uint8_t> payload;
    payload.reserve(24);
    const auto& uuid = this->getServiceUuid();
    payload.insert(payload.end(), uuid.data, uuid.data + 16);
    const auto& data = this->getServiceData();
    payload.insert(payload.end(), data.begin(), data.end());
    return std::map<std::uint16_t, sdbus::Variant>{ { 0xFFFF, sdbus::Variant(payload) } };
  };

  const auto typeGetter = []() {
    return std::string("broadcast");
  };

  object->addVTable(
    sdbus::registerProperty("ManufacturerData").withGetter(manufacturerDataGetter),
    sdbus::registerProperty("Type").withGetter(typeGetter),
    sdbus::registerMethod("Release").implementedAs(this->onRelease)
  ).forInterface("org.bluez.LEAdvertisement1");
};

const sdbus::ObjectPath& LinuxBleAdvertisementData::getObjectPath() const {
  return objectPath;
}

const boost::uuids::uuid& LinuxBleAdvertisementData::getServiceUuid() const {
  return serviceUuid;
}

const std::vector<std::uint8_t>& LinuxBleAdvertisementData::getServiceData() const {
  return serviceData;
}
}  // namespace clipbird
