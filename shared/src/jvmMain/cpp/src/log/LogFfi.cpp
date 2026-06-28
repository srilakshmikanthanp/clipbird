#include "log/LogFfi.hpp"

#include "log/Log.hpp"

extern "C" {

void clipbird_utility_configure_log() {
  clipbird::log::configureLogging();
}

}
