#include "log/Log.hpp"

#include <boost/log/utility/setup/common_attributes.hpp>
#include <boost/log/utility/setup/console.hpp>

#include <iostream>
#include <mutex>

namespace clipbird::log {

void configureLogging() {
  static std::once_flag configureOnce;
  std::call_once(configureOnce, []() {
    boost::log::add_common_attributes();
    boost::log::add_console_log(std::clog, boost::log::keywords::format = "[%TimeStamp%] [%Severity%] %Message%");
  });
}

}  // namespace clipbird::log
