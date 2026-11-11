#include "log/log.hxx"

namespace {

__attribute__((constructor))
void clipbird_log_configure_on_load() {
  com::srilakshmikanthanp::clipbird::log::initialize();
}

}  // namespace

extern "C" {

void clipbird_log_set_callback(clipbird_log_callback_t callback, void* context) {
  com::srilakshmikanthanp::clipbird::log::setCallback(callback, context);
}

void clipbird_log_clear_callback() {
  com::srilakshmikanthanp::clipbird::log::clearCallback();
}

}
