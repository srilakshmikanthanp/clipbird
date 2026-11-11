#include "error/error.hxx"

extern "C" {

const char* clipbird_error_last_error_message() {
  return com::srilakshmikanthanp::clipbird::error::getLastError().message.data();
}

int clipbird_error_last_error_code() {
  return com::srilakshmikanthanp::clipbird::error::getLastError().code;
}

}
