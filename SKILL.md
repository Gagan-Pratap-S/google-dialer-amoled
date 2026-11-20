---
name: google-dialer-amoled
description: >
  Decompile, modify, and build a pure pitch-black (#000000) AMOLED edition of any
  Google Phone (Google Dialer) APK. Focuses purely on visual theming, high-contrast
  readability, and silhouette avatar outline fixes without altering app functionality,
  layouts, or navigation. Supports both non-root (cloned standalone APK) and rooted
  (systemless Magisk/KernelSU module) devices.
---

# Google Dialer AMOLED Theme & Polish Skill

A focused, single-purpose skill to convert any stock or updated Google Phone (Dialer) APK into a pure pitch-black (`#000000`) AMOLED dark theme. 

> [!IMPORTANT]
> **Zero Layout / Functionality Modifications**:
> This skill strictly modifies visual color resources and styles. It **never** alters Java/Dalvik bytecode, method logic, bottom navigation docks, or layout XML files. Everything in Google Phone continues to behave exactly as designed by Google.

---

## 1. Non-Root vs Rooted Installation (Both Fully Supported)

| Installation Type | Target Device | How It Works | Output |
|---|---|---|---|
| **Non-Root (`--mode cloned`)** | Any standard unrooted phone | Clones package identifier to `com.google.android.dialer.amoled` and updates ContentProvider authorities so Android allows side-by-side installation without cryptographic signature conflicts. | Standalone `.apk` installed via `adb install` or file manager. |
| **Rooted (`--mode system`)** | Magisk / KernelSU / APatch | Preserves official package name (`com.google.android.dialer`) and packages into a systemless module that replaces `/product/priv-app/GoogleDialer/`. Bypasses signature checks completely. | Flashable `.zip` module flashed in Magisk/KernelSU app. |

---

## 2. Quick Start / CLI Usage

The automated CLI tool is located at:
`scripts/mod_dialer.py`

### Build for Non-Root (Default):
```bash
python scripts/mod_dialer.py build \
  --input /path/to/stock_GoogleDialer.apk \
  --output /path/to/GoogleDialer_AMOLED.apk \
  --mode cloned \
  --bump-version
```

### Build for Rooted Devices (Magisk / KernelSU Module):
```bash
python scripts/mod_dialer.py build \
  --input /path/to/stock_GoogleDialer.apk \
  --output /path/to/GoogleDialer_AMOLED.apk \
  --mode system \
  --magisk /path/to/GoogleDialer_AMOLED_Magisk.zip
```

---

## 3. What This Skill Modifies (Themes & Polish Only)

### A. Pure Pitch-Black (`#000000`) AMOLED Surfaces
Replaces stock Material You dark grey (`#1f1f1f`) surfaces in:
- `res/values-night/colors.xml`:
  - `settingslib_materialColorSurfaceContainer` &rarr; `#000000`
  - `settingslib_materialColorSurfaceContainerHigh` &rarr; `#0a0a0a`
  - `settingslib_materialColorSurfaceContainerHighest` &rarr; `#141414`
  - `settingslib_materialColorSurfaceVariant` &rarr; `#1f1f1f`
  - `bam_center_fragment_background` &rarr; `#000000`
  - `survey_surface_color` &rarr; `#000000`
  - `survey_alert_dialog_bg_color` &rarr; `#000000`
- `res/values-night-v31/colors.xml`:
  - `google_marterial_dialog_background_color` &rarr; `#000000`
  - `settingslib_dialog_background` &rarr; `#000000`

### B. High-Contrast Text & Readability
On pure `#000000` OLED displays, dark grey secondary text is difficult to read in bright daylight. The skill enhances readability:
- `dialpad_icon_text_enabled` &rarr; `#ffffffff` (pure white keypad letters & numbers)
- `dialpad_icon_text_disabled` &rarr; `#80ffffff`
- `empty_content_view_message` &rarr; `#ffffffff` (crisp call history empty states)
- `settingslib_materialColorOnSurface` &rarr; `#ffffffff`
- `settingslib_materialColorOnSurfaceVariant` &rarr; `#ffffffff`

### C. Silhouette Avatar Contrast Fix (Unsaved Contacts)
In pure AMOLED black, the stock hardcoded outline (`#ff2f3130`) for unsaved callers and service numbers blends into the background, making avatar circles appear blank or invisible.

Replaced with dynamic dark system outline `@android:color/system_outline_dark` in:
- `res/values-night-v34/colors.xml`
- `res/values-v34/colors.xml`
- `res/values-v34/styles.xml`

### D. Package Cloning (Non-Root Only)
For non-root mode, only `AndroidManifest.xml` is modified:
1. Package renamed: `com.google.android.dialer` &rarr; `com.google.android.dialer.amoled`
2. Custom permission renamed: `com.google.android.dialer.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` &rarr; `.amoled` suffix
3. Support library requirement relaxed: `<uses-library android:name="com.google.android.dialer.support" android:required="false"/>`
4. Provider authorities suffixed with `.amoled` to prevent authority collisions with pre-installed dialer.

---

## 4. Strict "Hands-Off" Rules

> [!WARNING]
> To ensure 100% stability in a single build with zero regressions:
> 1. **Do NOT touch navigation bars**: Never inject custom floating pills, liquid glass docks, or custom tabs. The stock 2-tab bottom dock is preserved.
> 2. **Do NOT touch layout XML dimensions**: Never modify dialpad margins, widths, or view hierarchies.
> 3. **Do NOT touch code/bytecode**: DEX files (`classes*.dex`) remain untouched unless running a separate recording-patch skill.

---

## 5. Troubleshooting Guide

| Issue / Error | Cause | Resolution |
|---|---|---|
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | Attempting to install system package name over factory Google app on non-root device. | Build with `--mode cloned` (installs alongside stock dialer). |
| `INSTALL_FAILED_VERSION_DOWNGRADE` | Installed dialer has a higher `versionCode`. | Pass `--bump-version` flag to automatically set `versionCode: 20999999`. |
| Unsaved contact silhouette invisible | Silhouette outline `#ff2f3130` matches AMOLED background. | Handled automatically by the silhouette fix in `values-night-v34`. |
| Apktool `NumberFormatException` | Quoted version code in `apktool.yml`. | The script writes `versionCode` strictly as a bare integer. |
