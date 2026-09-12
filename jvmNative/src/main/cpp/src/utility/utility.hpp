#pragma once

#include <spdlog/spdlog.h>

#include <exception>
#include <type_traits>

namespace clipbird::utility {
template <typename F>
void logOnThrow(const char* context, F&& fn) noexcept {
  try {
    fn();
  } catch (const std::exception& e) {
    spdlog::warn("{}: {}", context, e.what());
  } catch (...) {
    spdlog::warn("{}: unknown error", context);
  }
}

template <typename E>
constexpr auto toUnderlying(E e) noexcept {
  return static_cast<std::underlying_type_t<E>>(e);
}
}
