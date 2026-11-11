#pragma once

#include <memory>

#include "fs/DirectoryInfo.hpp"

namespace com::srilakshmikanthanp::clipbird::fs {

std::unique_ptr<DirectoryInfo> createDirectoryInfo();

}  // namespace com::srilakshmikanthanp::clipbird::fs
