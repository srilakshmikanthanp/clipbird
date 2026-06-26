#pragma once

#include "error/Error.hpp"

extern "C" {

const char* clipbird_error_last_error_message();
int clipbird_error_last_error_code();

}
