#include "error/Error.hpp"

#include <string>

namespace {
thread_local clipbird::error::Error g_last_error = {};
}

namespace clipbird::error {
void setLastError(int code, std::string_view error) {
  g_last_error.code = code;
  g_last_error.message = error;
}

void setLastError(Error error) {
  g_last_error.code = error.code;
  g_last_error.message = error.message;
}

void clearLastError() {
  g_last_error = {};
}

Error& getLastError() {
  return g_last_error;
}
}  // namespace clipbird::ffi
