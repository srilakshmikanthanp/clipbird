#pragma once

#include <map>
#include <optional>
#include <string>
#include <vector>

#include <boost/uuid/uuid.hpp>
#include <boost/uuid/string_generator.hpp>
#include <sdbus-c++/sdbus-c++.h>

namespace clipbird::bluetooth {

using LinuxStdBusProperties = std::map<sdbus::PropertyName, sdbus::Variant>;

template <typename T>
T fromVariant(const sdbus::Variant& variant) {
	return variant.get<T>();
}

template <>
inline std::vector<boost::uuids::uuid> fromVariant<std::vector<boost::uuids::uuid>>(const sdbus::Variant& variant) {
  boost::uuids::string_generator generator;
  std::vector<boost::uuids::uuid> result;

  for (const auto& uuid : variant.get<std::vector<std::string>>()) {
    result.push_back(generator(uuid));
  }

  return result;
}

template <typename T>
std::optional<T> getProperty(const LinuxStdBusProperties& properties, const std::string& name) {
	auto property = properties.find(sdbus::PropertyName(name));

	if (property == properties.end()) {
		return std::nullopt;
	}

	return fromVariant<T>(property->second);
}

}  // namespace clipbird::bluetooth
