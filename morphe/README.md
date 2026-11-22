# Morphe Manager Patches for Google Phone

This directory contains native Kotlin patch sources compatible with the [Morphe Patcher](https://github.com/MorpheApp/morphe-patcher) and [Morphe Manager](https://github.com/MorpheApp/morphe-manager) ecosystem (as well as ReVanced-compatible patchers).

---

## Included Patches

1. **`GoogleDialerThemePatch.kt` (`AMOLED Dark Theme`)**:
   - Replaces stock Material You dark greys with `#000000` pitch black across all surfaces.
   - Sets keypad digits and empty states to high-contrast white (`#ffffffff`).
   - Fixes unsaved contact silhouette outlines with `@android:color/system_outline_dark`.

2. **`GoogleDialerClonePatch.kt` (`Clone Dialer Package`)**:
   - Optional toggle for **non-root users**.
   - Clones the package name to `com.google.android.dialer.amoled` and updates provider authorities so Android allows installing it alongside the factory system dialer.

---

## How to Build & Use

### Option A: Submitting to Morphe Patches
If you want these patches available to every Morphe Manager user globally:
1. Fork [MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches).
2. Place these files under `src/main/kotlin/app/morphe/patches/google/dialer/`.
3. Submit a Pull Request.

### Option B: Building a Custom Patch Bundle
1. Clone the `morphe-patches` repository.
2. Add these patch files into the project.
3. Build the patches bundle:
   ```bash
   ./gradlew build
   ```
4. Load the generated `.jar` file into **Morphe Manager** under **Settings &rarr; Sources &rarr; Local Patches**.
5. Select your Google Phone APK, toggle **AMOLED Dark Theme**, tap **Patch**, and install.

---

## Important Tips

- **Use Universal / Non-Pixel APK Only**: Never patch Pixel-only APKs (tagged with `-pixel` or `pixel2026`). They crash on Motorola, Samsung, Xiaomi, and other non-Pixel devices due to missing Pixel proprietary system frameworks. Use universal/non-nexus builds.
- **Default Phone App & Restricted Settings**:
  On Android 13/14+, if you cannot select the cloned app as your default phone app:
  1. Open **Settings → Apps → All Apps → Phone (AMOLED)**.
  2. Tap the **three dots (⋮)** in the top right corner.
  3. Tap **"Allow restricted settings"** and authenticate.
  4. Now select it under **Default apps → Phone app** and grant Phone/Contacts permissions.
