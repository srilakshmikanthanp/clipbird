#pragma once

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum clipbird_fs_directory_info_error_code {
  CLIPBIRD_FS_DIRECTORY_INFO_INVALID_ARGUMENT = 0,
  CLIPBIRD_FS_DIRECTORY_INFO_INTERNAL_ERROR = 1,
} clipbird_fs_directory_info_error_code_t;

/**
 * Checks whether the file system holding the directory at path is mounted noexec, i.e. files in
 * it cannot be executed or loaded as shared libraries.
 * On success, stores the result in is_noexec and returns true. On failure, returns false; the error
 * code and message are available through the error API.
 */
bool clipbird_fs_directory_info_is_noexec(const char* path, bool* is_noexec);

#ifdef __cplusplus
}
#endif
