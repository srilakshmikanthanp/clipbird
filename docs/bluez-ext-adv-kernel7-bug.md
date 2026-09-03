# BlueZ Extended Advertising Bug with Linux Kernel 7.0

## Summary

BLE advertisement registration (`org.bluez.LEAdvertisingManager1.RegisterAdvertisement`) fails
with `org.bluez.Error.Failed: Failed to register advertisement` on systems running BlueZ 5.72
with Linux kernel 7.0 (Ubuntu 24.04 HWE kernel `7.0.0-30-generic`).

## Root Cause

BlueZ has a long-standing bug in `src/advertising.c` (`add_adv_params_callback`) where it uses
the wrong struct size when computing the parameter length for `MGMT_OP_ADD_EXT_ADV_DATA`:

```c
// Bug (line ~1367 in BlueZ 5.72 advertising.c):
param_len = sizeof(struct mgmt_cp_add_advertising)   // = 11 bytes  ← WRONG
            + adv_data_len + scan_rsp_len;

// Correct:
param_len = sizeof(struct mgmt_cp_add_ext_adv_data)  // = 3 bytes
            + adv_data_len + scan_rsp_len;
```

The two structs:
- `mgmt_cp_add_advertising` (legacy `MGMT_OP_ADD_ADVERTISING = 0x003E`): 11-byte header
- `mgmt_cp_add_ext_adv_data` (extended `MGMT_OP_ADD_EXT_ADV_DATA = 0x0055`): 3-byte header

BlueZ sends `11 + N` bytes but the kernel expects exactly `3 + N` bytes.

## Why It Broke with Kernel 7.0

Older kernels (≤ 6.11) checked `len >= MGMT_ADD_EXT_ADV_DATA_SIZE + adv_data_len + scan_rsp_len`,
so the 8 extra bytes were silently ignored. Linux kernel 7.0 tightened this to a strict equality
check (`len == 3 + adv_data_len + scan_rsp_len`), causing the kernel to return
`MGMT_STATUS_INVALID_PARAMS (0x0d)` for every `RegisterAdvertisement` call.

## How We Diagnosed It

1. Enabled bluetoothd debug logging via systemd override (`ExecStart=/usr/libexec/bluetooth/bluetoothd -d`)
2. Found log line: `src/advertising.c:add_client_complete() Failed to add advertisement: Invalid Parameters (0x0d)`
3. Confirmed `MGMT_OP_ADD_EXT_ADV_DATA = 0x0055` (kernel header) and traced the disassembly of
   `/usr/libexec/bluetooth/bluetoothd` to offset `0x9c673`:
   ```
   lea    0xb(%r14,%r15,1),%eax   ; param_len = adv_data_len + scan_rsp_len + 11 (wrong: should be +3)
   ```

## Affected Versions

- **BlueZ**: 5.72-0ubuntu5.5 (Ubuntu 24.04) — bug present in binary at file offset `0x9c677`
- **Kernel**: 7.0.0-30-generic (Ubuntu 24.04 HWE) — introduced strict length validation
- **Trigger**: Any D-Bus client calling `RegisterAdvertisement` on adapters where
  `MaxAdvLen > 31` (triggers extended advertising path instead of legacy)

## Workarounds

### Option A — Boot with kernel 6.11 (no code changes)

Kernel 6.11 still uses the permissive `>=` check. Select it at GRUB or set as default:

```bash
sudo sed -i 's/GRUB_DEFAULT=.*/GRUB_DEFAULT="Advanced options for Ubuntu>Ubuntu, with Linux 6.11.0-17-generic"/' /etc/default/grub
sudo update-grub
```

### Option B — One-byte binary patch (no reboot, survives only until bluez is upgraded)

Changes byte at file offset `640631` (`0x9c677`) from `0x0b` to `0x03`:

```bash
sudo cp /usr/libexec/bluetooth/bluetoothd /usr/libexec/bluetooth/bluetoothd.orig
printf '\x03' | sudo dd of=/usr/libexec/bluetooth/bluetoothd bs=1 seek=640631 count=1 conv=notrunc
sudo systemctl restart bluetooth
```

### Option C — Rebuild BlueZ from source with the fix

```bash
echo "deb-src https://archive.ubuntu.com/ubuntu/ noble main" | sudo tee -a /etc/apt/sources.list
sudo apt update && sudo apt source bluez && sudo apt build-dep bluez
# Edit bluez-5.72/src/advertising.c line ~1367:
#   sizeof(struct mgmt_cp_add_advertising) → sizeof(struct mgmt_cp_add_ext_adv_data)
cd bluez-5.72 && dpkg-buildpackage -us -uc -b
sudo dpkg -i ../bluez_*.deb
```

## Upstream Fix

Report to:
- BlueZ upstream: https://github.com/bluez/bluez/issues
- Ubuntu: https://bugs.launchpad.net/ubuntu/+source/bluez

The fix is one line in `src/advertising.c` in the `add_adv_params_callback` function.
Once fixed upstream, Ubuntu will backport it and a normal `apt upgrade` will resolve the issue.