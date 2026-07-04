#pragma once

#ifdef __cplusplus
extern "C" {
#endif

typedef struct clipbird_bluetooth_manager clipbird_bluetooth_manager_t;

/**
 * Creates a new Bluetooth manager instance.
 * @return A pointer to the newly created manager instance.
 */
clipbird_bluetooth_manager_t* clipbird_bluetooth_manager_create(void);

#ifdef __cplusplus
}
#endif
