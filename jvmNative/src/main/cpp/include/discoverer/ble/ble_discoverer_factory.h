#pragma once

#include <stdint.h>

#include "discoverer/discoverer.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef void (*clipbird_on_discovery_started_t)(void* context);
typedef void (*clipbird_on_device_discovered_t)(int64_t device_id, void* context);
typedef void (*clipbird_on_discovery_failed_t)(int code, const char* reason, void* context);
typedef void (*clipbird_on_discovery_stopped_t)(void* context);

typedef struct clipbird_discoverer_ble_listener {
  clipbird_on_discovery_started_t  on_started;
  clipbird_on_device_discovered_t  on_device_discovered;
  clipbird_on_discovery_failed_t   on_failed;
  clipbird_on_discovery_stopped_t  on_stopped;
  void*                            context;
} clipbird_discoverer_ble_listener_t;

typedef enum clipbird_discoverer_ble_factory_error_code {
  CLIPBIRD_DISCOVERER_BLE_FACTORY_INTERNAL_ERROR = 1
} clipbird_discoverer_ble_factory_error_code_t;

/**
 * Creates a new BLE discoverer instance.
 * @param service_uuid The UUID used to filter clipbird advertisements (UUID string format).
 * @param listener Struct of callback pointers and opaque context forwarded to each one.
 * @return A pointer to the created discoverer instance, or nullptr on failure.
 */
clipbird_discoverer_t* clipbird_discoverer_ble_create(
  const char* service_uuid,
  const clipbird_discoverer_ble_listener_t* listener
);

#ifdef __cplusplus
}
#endif
