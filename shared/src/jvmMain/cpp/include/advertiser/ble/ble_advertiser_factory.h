#pragma once

#include <stdint.h>

#include "advertiser/advertiser.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum clipbird_advertiser_ble_factory_error_code {
  CLIPBIRD_ADVERTISER_BLE_FACTORY_INTERNAL_ERROR = 1
} clipbird_advertiser_ble_factory_error_code_t;

/**
 * Creates a new BLE advertiser instance.
 * @param service_uuid The UUID of the BLE service to advertise.
 * @param service_data The service data to include in the advertisement.
 * @param service_data_length The length of the service data.
 * @return A pointer to the created advertiser instance, or nullptr on failure.
 */
clipbird_advertiser_t* clipbird_advertiser_ble_create(const char* service_uuid, const uint8_t* service_data, int service_data_length);

#ifdef __cplusplus
}
#endif
