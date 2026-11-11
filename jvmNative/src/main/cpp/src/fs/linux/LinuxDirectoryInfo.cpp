#include "fs/linux/LinuxDirectoryInfo.hpp"

#include <sys/statvfs.h>

#include <cerrno>
#include <system_error>

namespace com::srilakshmikanthanp::clipbird::fs {

bool LinuxDirectoryInfo::isNoexec(const std::filesystem::path& path) const {
  struct statvfs info {};

  if (statvfs(path.c_str(), &info) != 0) {
    throw std::system_error(errno, std::generic_category(), "statvfs failed for " + path.string());
  }

  return (info.f_flag & ST_NOEXEC) != 0;
}

}  // namespace com::srilakshmikanthanp::clipbird::fs
