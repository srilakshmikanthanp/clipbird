#pragma once

#include "log/log.h"

namespace com::srilakshmikanthanp::clipbird::log {

void initialize();
void setCallback(clipbird_log_callback_t callback, void* context);
void clearCallback();

}  // namespace com::srilakshmikanthanp::clipbird::log
