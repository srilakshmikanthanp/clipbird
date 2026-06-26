#pragma once

#include <string_view>

namespace clipbird::error {

struct Error {
  int code;
  std::string_view message;
};

void setLastError(int code, std::string_view error);
void setLastError(Error error);
void clearLastError();
Error& getLastError();

}  // namespace clipbird::error
