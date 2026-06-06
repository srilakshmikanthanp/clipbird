#include "bluetooth/ble/BleAdvertiserFactory.hpp"

extern "C" {
/**
 * Opaque structure representing a BLE advertiser instance. The actual implementation is hidden from the user and managed internally.
 */
typedef struct ClipBirdAdvertiser ClipBirdAdvertiser;

/**
 * Creates a new BLE advertiser instance.
 * @param service_uuid The UUID of the BLE service to advertise.
 * @param service_data The service data to include in the advertisement.
 * @param service_data_length The length of the service data.
 * @return A pointer to the created ClipBirdAdvertiser instance, or nullptr on failure.
 */
ClipBirdAdvertiser* clipbird_ble_advertiser_create(const char* service_uuid, const uint8_t* service_data, int service_data_length);

/**
 * Destroys a BLE advertiser instance.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to destroy.
 */
void clipbird_ble_advertiser_destroy(ClipBirdAdvertiser* advertiser);

/**
 * Checks if the specified advertiser instance is currently advertising.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to check.
 * @param is_advertising A pointer to a boolean variable that will be set to true if advertising, or false otherwise.
 * @return 0 on success, or a non-zero error code on failure.
 */
int clipbird_ble_advertiser_is_advertising(const ClipBirdAdvertiser* advertiser, bool* is_advertising);

/**
 * Starts BLE advertising using the specified advertiser instance.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to use for advertising.
 * @return 0 on success, or a non-zero error code on failure.
 */
int clipbird_ble_advertiser_start(ClipBirdAdvertiser* advertiser);

/**
 * Stops BLE advertising for the specified advertiser instance.
 * @param advertiser A pointer to the ClipBirdAdvertiser instance to stop advertising.
 * @return 0 on success, or a non-zero error code on failure.
 */
int clipbird_ble_advertiser_stop(ClipBirdAdvertiser* advertiser);

/**
 * Retrieves the last error message from the BLE advertiser operations.
 * @return A pointer to a null-terminated string containing the last error message.
 */
const char* clipbird_ble_advertiser_last_error();
}
