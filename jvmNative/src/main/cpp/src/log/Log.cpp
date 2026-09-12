#include "log/Log.hpp"
#include "log/backend/LogCallbackBackend.hpp"

#include <spdlog/spdlog.h>

#include <memory>
#include <mutex>

namespace clipbird::log {

namespace {
  std::shared_ptr<LogCallbackBackend> callbackBackend = std::make_shared<LogCallbackBackend>();
  std::once_flag flag;
}

void initialize() {
  std::call_once(flag, []() {
    auto logger = std::make_shared<spdlog::logger>("clipbird", callbackBackend);
    logger->set_level(spdlog::level::trace);
    spdlog::set_default_logger(logger);
  });
}

void setCallback(const clipbird_log_callback_t cb, void* context) {
  callbackBackend->setCallback(cb, context);
  spdlog::info("Log callback registered");
}

void clearCallback() {
  callbackBackend->clearCallback();
}

}  // namespace clipbird::log
