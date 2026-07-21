#include "log/Log.hpp"
#include "log/backend/LogCallbackBackend.hpp"

#include <boost/log/core.hpp>
#include <boost/log/expressions.hpp>
#include <boost/log/sinks/sync_frontend.hpp>
#include <boost/log/utility/setup/common_attributes.hpp>
#include <boost/make_shared.hpp>

#include <mutex>

namespace clipbird::log {

namespace expr = boost::log::expressions;
namespace logging = boost::log;
namespace sinks = boost::log::sinks;

namespace {
  boost::shared_ptr<LogCallbackBackend> callbackBackend = boost::make_shared<LogCallbackBackend>();
  std::once_flag flag;
}

void initialize() {
  std::call_once(flag, []() {
    auto sink = boost::make_shared<sinks::synchronous_sink<LogCallbackBackend>>(callbackBackend);
    boost::log::add_common_attributes();
    sink->set_formatter(expr::stream << expr::smessage);
    logging::core::get()->add_sink(sink);
  });
}

void setCallback(const clipbird_log_callback_t cb, void* context) {
  callbackBackend->setCallback(cb, context);
}

void clearCallback() {
  callbackBackend->clearCallback();
}

}  // namespace clipbird::log
