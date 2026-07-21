#include "log/backend/LogCallbackBackend.hpp"

#include <boost/log/attributes/attribute_value_set.hpp>
#include <boost/log/attributes/value_extraction.hpp>

namespace logging = boost::log;

namespace clipbird::log {

clipbird_log_level_t LogCallbackBackend::toLevel(const logging::trivial::severity_level severity) {
  switch (severity) {
    case logging::trivial::trace:
      return CLIPBIRD_LOG_LEVEL_TRACE;
    case logging::trivial::debug:
      return CLIPBIRD_LOG_LEVEL_DEBUG;
    case logging::trivial::info:
      return CLIPBIRD_LOG_LEVEL_INFO;
    case logging::trivial::warning:
      return CLIPBIRD_LOG_LEVEL_WARNING;
    case logging::trivial::error:
      return CLIPBIRD_LOG_LEVEL_ERROR;
    case logging::trivial::fatal:
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

void LogCallbackBackend::consume(const logging::record_view& rec, const string_type& formatted) {
  auto logCallback = callback.load(std::memory_order_acquire);
  auto context = callbackContext.load(std::memory_order_acquire);

  if (!logCallback) {
    return;
  }

  const auto extractedSeverity = logging::extract<logging::trivial::severity_level>("Severity", rec);
  const auto severity = extractedSeverity ? extractedSeverity.get() : logging::trivial::info;

  const auto level = toLevel(severity);
  const auto message = formatted.c_str();
  logCallback(level, message, context);
}

}  // namespace clipbird::log
