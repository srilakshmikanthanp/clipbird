#include "log/log.hxx"

extern "C" {

void clipbird_log_configure() {
  clipbird::log::configure();
}

}
