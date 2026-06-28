#include "LinuxBleAdvertisementData.hpp"

#include <boost/uuid/uuid_io.hpp>

namespace clipbird::bluetooth::ble {
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

  using serviceUuidData = std::map<std::string, std::vector<std::uint8_t>>;
  using serviceUuidList = std::vector<std::string>;

  const auto serviceUuidGetter = [this]() { return serviceUuidList{ boost::uuids::to_string(this->getServiceUuid()) }; };
  const auto serviceDataGetter = [this]() { return serviceUuidData{ { boost::uuids::to_string(this->getServiceUuid()), this->getServiceData() } }; };
  const auto typeGetter = [this]() { return std::string("broadcast"); };

  object->addVTable(
    sdbus::registerProperty("ServiceUUIDs").withGetter(serviceUuidGetter),
    sdbus::registerProperty("ServiceData").withGetter(serviceDataGetter),
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
