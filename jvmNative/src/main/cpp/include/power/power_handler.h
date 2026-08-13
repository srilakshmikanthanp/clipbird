#pragma once

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_power_handler clipbird_power_handler_t;

typedef enum clipbird_power_handler_error_code {
  CLIPBIRD_POWER_HANDLER_INVALID_ARGUMENT = 0,
  CLIPBIRD_POWER_HANDLER_INTERNAL_ERROR = 1,
} clipbird_power_handler_error_code_t;

typedef void (*clipbird_power_handler_callback_t)(
  void* context
);

clipbird_power_handler_t* clipbird_power_handler_create(
  clipbird_power_handler_callback_t on_sleep,
  clipbird_power_handler_callback_t on_wake,
  void* context
);

void clipbird_power_handler_destroy(
  clipbird_power_handler_t* handler
);

#ifdef __cplusplus
}
#endif
