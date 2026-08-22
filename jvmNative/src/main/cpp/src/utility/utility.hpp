#pragma once

#include <boost/log/trivial.hpp>

#include <exception>
#include <type_traits>

namespace clipbird::utility {
template <typename F>
void logOnThrow(const char* context, F&& fn) noexcept {
  try {
    fn();
  } catch (const std::exception& e) {
    BOOST_LOG_TRIVIAL(warning) << context << ": " << e.what();
  } catch (...) {
    BOOST_LOG_TRIVIAL(warning) << context << ": unknown error";
  }
}

template <typename E>
constexpr auto toUnderlying(E e) noexcept {
  return static_cast<std::underlying_type_t<E>>(e);
}
}
