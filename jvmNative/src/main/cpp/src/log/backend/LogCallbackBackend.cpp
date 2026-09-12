#include "log/backend/LogCallbackBackend.hpp"

#include <string>

namespace clipbird::log {

clipbird_log_level_t LogCallbackBackend::toLevel(const spdlog::level::level_enum level) {
  switch (level) {
    case spdlog::level::trace:
      return CLIPBIRD_LOG_LEVEL_TRACE;
    case spdlog::level::debug:
      return CLIPBIRD_LOG_LEVEL_DEBUG;
    case spdlog::level::info:
      return CLIPBIRD_LOG_LEVEL_INFO;
    case spdlog::level::warn:
      return CLIPBIRD_LOG_LEVEL_WARNING;
    case spdlog::level::err:
      return CLIPBIRD_LOG_LEVEL_ERROR;
    case spdlog::level::critical:
      return CLIPBIRD_LOG_LEVEL_FATAL;
    default:
      return CLIPBIRD_LOG_LEVEL_INFO;
  }
}

void LogCallbackBackend::setCallback(const clipbird_log_callback_t callback, void* context) {
  this->callbackContext.store(context, std::memory_order_release);
  this->callback.store(callback, std::memory_order_release);
}

void LogCallbackBackend::clearCallback() {
  callback.store(nullptr, std::memory_order_release);
  callbackContext.store(nullptr, std::memory_order_release);
}

void LogCallbackBackend::sink_it_(const spdlog::details::log_msg& msg) {
  auto logCallback = callback.load(std::memory_order_acquire);
  auto context = callbackContext.load(std::memory_order_acquire);

  if (!logCallback) {
    return;
  }

  const auto level = toLevel(msg.level);
  std::string message(msg.payload.data(), msg.payload.size());
  logCallback(level, message.c_str(), context);
}

void LogCallbackBackend::flush_() {}

}  // namespace clipbird::log
