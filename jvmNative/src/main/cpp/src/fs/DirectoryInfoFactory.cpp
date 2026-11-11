#include "fs/DirectoryInfoFactory.hpp"

#if defined(__linux__)
#include "linux/LinuxDirectoryInfo.hpp"
#elif defined(_WIN32)
  #error "Directory info is not implemented on Windows."
#else
  #error "Directory info is not implemented on this platform."
#endif

namespace com::srilakshmikanthanp::clipbird::fs {

std::unique_ptr<DirectoryInfo> createDirectoryInfo() {
  return std::make_unique<LinuxDirectoryInfo>();
}

}  // namespace com::srilakshmikanthanp::clipbird::fs
