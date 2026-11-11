#pragma once

#include <filesystem>

namespace com::srilakshmikanthanp::clipbird::fs {

class DirectoryInfo {
 public:
  virtual ~DirectoryInfo() = default;

  /**
   * Returns whether the file system holding [path] is mounted noexec.
   * Throws std::system_error when the file system cannot be queried.
   */
  virtual bool isNoexec(const std::filesystem::path& path) const = 0;
};

}  // namespace com::srilakshmikanthanp::clipbird::fs
