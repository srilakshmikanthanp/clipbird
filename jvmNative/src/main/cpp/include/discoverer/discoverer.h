#pragma once

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_discoverer clipbird_discoverer_t;

typedef enum clipbird_discoverer_error_code {
  CLIPBIRD_DISCOVERER_INVALID_ARGUMENT = 0,
  CLIPBIRD_DISCOVERER_INTERNAL_ERROR   = 1
} clipbird_discoverer_error_code_t;

/**
 * Starts BLE discovery using the specified discoverer instance.
 * @param discoverer A pointer to the discoverer instance.
 * @return true on success, false on failure.
 */
bool clipbird_discoverer_start(clipbird_discoverer_t* discoverer);

/**
 * Stops BLE discovery for the specified discoverer instance.
 * @param discoverer A pointer to the discoverer instance.
 * @return true on success, false on failure.
 */
bool clipbird_discoverer_stop(clipbird_discoverer_t* discoverer);

/**
 * Destroys a BLE discoverer instance.
 * @param discoverer A pointer to the discoverer instance to destroy.
 */
void clipbird_discoverer_destroy(clipbird_discoverer_t* discoverer);

#ifdef __cplusplus
}
#endif
