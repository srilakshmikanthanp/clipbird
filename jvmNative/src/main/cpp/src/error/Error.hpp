#pragma once

#include <string>
#include <string_view>

namespace com::srilakshmikanthanp::clipbird::error {

struct Error {
  int code;
  std::string message;
};

void setLastError(int code, std::string_view error);
void setLastError(Error error);
void clearLastError();
Error& getLastError();

}  // namespace com::srilakshmikanthanp::clipbird::error
