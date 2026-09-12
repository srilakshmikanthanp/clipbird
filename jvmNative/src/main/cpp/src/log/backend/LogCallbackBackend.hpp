#pragma once

#include "log/log.h"

#include <spdlog/sinks/base_sink.h>

#include <atomic>
#include <mutex>

namespace clipbird::log {

class LogCallbackBackend : public spdlog::sinks::base_sink<std::mutex> {
 private:
  static clipbird_log_level_t toLevel(spdlog::level::level_enum level);

 private:
  std::atomic<clipbird_log_callback_t> callback{nullptr};
  std::atomic<void*> callbackContext{nullptr};

 protected:
  void sink_it_(const spdlog::details::log_msg& msg) override;
  void flush_() override;

 public:
  LogCallbackBackend() = default;
  ~LogCallbackBackend() override = default;

  void setCallback(clipbird_log_callback_t callback, void* context);
  void clearCallback();
};

}  // namespace clipbird::log
