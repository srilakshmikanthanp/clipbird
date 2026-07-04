#include "log/log.hxx"

extern "C" {

void clipbird_configure_log() {
  clipbird::log::configureLogging();
}

}
