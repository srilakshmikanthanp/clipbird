#pragma once

#include "fs/DirectoryInfo.hpp"

namespace com::srilakshmikanthanp::clipbird::fs {

class LinuxDirectoryInfo final : public DirectoryInfo {
 public:
  bool isNoexec(const std::filesystem::path& path) const override;
};

}  // namespace com::srilakshmikanthanp::clipbird::fs
