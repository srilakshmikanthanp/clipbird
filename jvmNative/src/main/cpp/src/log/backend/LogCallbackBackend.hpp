#pragma once

#include "log/log.h"

#include <boost/log/core/record_view.hpp>
#include <boost/log/sinks/basic_sink_backend.hpp>
#include <boost/log/trivial.hpp>

#include <atomic>

namespace clipbird::log {

class LogCallbackBackend : public boost::log::sinks::basic_formatted_sink_backend<char, boost::log::sinks::synchronized_feeding> {
 private:
  static clipbird_log_level_t toLevel(boost::log::trivial::severity_level severity);

 private:
  std::atomic<clipbird_log_callback_t> callback{nullptr};
  std::atomic<void*> callbackContext{nullptr};

 public:
  void setCallback(clipbird_log_callback_t callback, void* context);
  void clearCallback();
  void consume(const boost::log::record_view& rec, const string_type& formatted);
};

}  // namespace clipbird::log
