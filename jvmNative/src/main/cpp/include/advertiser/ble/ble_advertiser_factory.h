#pragma once

#include <stdint.h>

#include "advertiser/advertiser.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef void (*clipbird_on_advertising_started_t)(void* context);
typedef void (*clipbird_on_advertising_failed_t)(int code, const char* reason, void* context);
typedef void (*clipbird_on_advertising_stopped_t)(void* context);

typedef struct clipbird_advertiser_ble_listener {
  clipbird_on_advertising_started_t on_started;
  clipbird_on_advertising_failed_t  on_failed;
  clipbird_on_advertising_stopped_t on_stopped;
  void*                             context;
} clipbird_advertiser_ble_listener_t;

typedef enum clipbird_advertiser_ble_factory_error_code {
  CLIPBIRD_ADVERTISER_BLE_FACTORY_INTERNAL_ERROR = 1
} clipbird_advertiser_ble_factory_error_code_t;

/**
 * Creates a new BLE advertiser instance.
 * @param service_uuid The UUID of the BLE service to advertise.
 * @param service_data The service data to include in the advertisement.
 * @param service_data_length The length of the service data.
 * @param listener Struct of callback pointers and opaque user_data forwarded to each one.
 * @return A pointer to the created advertiser instance, or nullptr on failure.
 */
clipbird_advertiser_t* clipbird_advertiser_ble_create(
  const char* service_uuid,
  const uint8_t* service_data,
  int service_data_length,
  const clipbird_advertiser_ble_listener_t* listener
);

#ifdef __cplusplus
}
#endif
