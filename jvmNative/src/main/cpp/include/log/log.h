#pragma once

#ifdef __cplusplus
extern "C" {
#endif

typedef enum clipbird_log_level {
	CLIPBIRD_LOG_LEVEL_TRACE = 0,
	CLIPBIRD_LOG_LEVEL_DEBUG = 1,
	CLIPBIRD_LOG_LEVEL_INFO = 2,
	CLIPBIRD_LOG_LEVEL_WARNING = 3,
	CLIPBIRD_LOG_LEVEL_ERROR = 4,
	CLIPBIRD_LOG_LEVEL_FATAL = 5
} clipbird_log_level_t;

typedef void (*clipbird_log_callback_t)(clipbird_log_level_t level, const char* message, void* context);

void clipbird_log_set_callback(clipbird_log_callback_t callback, void* context);
void clipbird_log_clear_callback(void);

#ifdef __cplusplus
}
#endif
