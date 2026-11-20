# Google Dialer AMOLED Theme

A clean, reproducible Antigravity skill and CLI tool to convert any stock Google Phone (Google Dialer) APK into a pure pitch-black (`#000000`) AMOLED theme for OLED displays.

## Highlights
- **100% True AMOLED Black (`#000000`)**: Saves battery and looks vibrant on OLED screens.
- **High-Contrast Readability**: White typography for dialpad digits, letters, and empty-state messaging.
- **Preserves Original Navigation & Layout**: Retains Google's stock bottom 2-tab navigation dock.
- **Root & Non-Root Support**:
  - **Non-Root**: Builds a standalone cloned APK (`com.google.android.dialer.amoled`) that installs alongside stock dialer without signature conflicts.
  - **Root**: Builds a systemless Magisk / KernelSU / APatch module for `/product/priv-app/GoogleDialer/`.
- **Zero Bytecode/Logic Alteration**: Modifies only color/style resources. No Java/Dalvik code changes.

## Prerequisites
- Python 3.8+
- Java Runtime (JRE 17+)
- `apktool` (v2.9.0+)
- `uber-apk-signer` (or `apksigner`)

## Usage

### 1. Build for Non-Root Phones (Standalone APK)
```bash
python scripts/mod_dialer.py build \
  --input /path/to/stock_GoogleDialer.apk \
  --output ./GoogleDialer_AMOLED.apk \
  --mode cloned \
  --bump-version
```

### 2. Build for Rooted Phones (Systemless Magisk Module)
```bash
python scripts/mod_dialer.py build \
  --input /path/to/stock_GoogleDialer.apk \
  --output ./GoogleDialer_System.apk \
  --mode system \
  --magisk ./GoogleDialer_AMOLED_Magisk.zip
```

## License
MIT
