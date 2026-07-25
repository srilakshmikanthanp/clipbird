#pragma once

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_advertiser clipbird_advertiser_t;

typedef enum clipbird_advertiser_error_code {
	CLIPBIRD_ADVERTISER_INVALID_ARGUMENT = 0,
	CLIPBIRD_ADVERTISER_INTERNAL_ERROR = 1
} clipbird_advertiser_error_code_t;

/**
 * Starts BLE advertising using the specified advertiser instance.
 * @param advertiser A pointer to the advertiser instance to use for advertising.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_advertiser_start(clipbird_advertiser_t* advertiser);

/**
 * Stops BLE advertising for the specified advertiser instance.
 * @param advertiser A pointer to the advertiser instance to stop advertising.
 * @return true on success, or false on failure. In case of failure, the last error can be retrieved using the error handling functions.
 */
bool clipbird_advertiser_stop(clipbird_advertiser_t* advertiser);

/**
 * Destroys a BLE advertiser instance.
 * @param advertiser A pointer to the advertiser instance to destroy.
 */
void clipbird_advertiser_destroy(clipbird_advertiser_t* advertiser);

#ifdef __cplusplus
}
#endif
