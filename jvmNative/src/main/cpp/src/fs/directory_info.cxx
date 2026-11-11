#include "fs/directory_info.hxx"

#include "error/Error.hpp"

#include <exception>

namespace error = com::srilakshmikanthanp::clipbird::error;
namespace fs = com::srilakshmikanthanp::clipbird::fs;

extern "C" {

bool clipbird_fs_directory_info_is_noexec(const char* path, bool* is_noexec) {
  if (!path || !is_noexec) {
    error::setLastError(CLIPBIRD_FS_DIRECTORY_INFO_INVALID_ARGUMENT, "path and is_noexec must not be null.");
    return false;
  }

  try {
    *is_noexec = fs::createDirectoryInfo()->isNoexec(path);
    error::clearLastError();
    return true;
  } catch (const std::exception& e) {
    error::setLastError(CLIPBIRD_FS_DIRECTORY_INFO_INTERNAL_ERROR, e.what());
    return false;
  } catch (...) {
    error::setLastError(CLIPBIRD_FS_DIRECTORY_INFO_INTERNAL_ERROR, "Unknown error reading directory info.");
    return false;
  }
}

}  // extern "C"
