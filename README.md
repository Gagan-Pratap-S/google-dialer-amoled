# Google Dialer AMOLED Theme Modder

Turn any official Google Phone (Google Dialer) APK into a clean, true pitch-black (`#000000`) AMOLED theme.

Google's default dark mode uses Material You dark grey (`#1f1f1f`). This project repacks the APK with true black backgrounds across all surfaces, sets keypad digits to high-contrast white so they stay readable in bright sunlight, and fixes the silhouette outline on unsaved contact thumbnails.

> **Zero code or layout changes**: This mod only changes color and style resources. All layouts, views, and underlying app logic remain 100% stock.

---

## What It Changes

- **Pure AMOLED Black (`#000000`)**: True black backgrounds across dialer home, keypad, settings, and dialogs.
- **High-Contrast Readability**: Dialpad numbers and letters are set to pure white (`#ffffffff`) so they don't look washed out.
- **Silhouette Avatar Fix**: Restores `@android:color/system_outline_dark` so unsaved contact thumbnails don't vanish into the black background.

---

## Root vs Non-Root

| | Non-Root (Most Users) | Rooted (Magisk / KernelSU / APatch) |
|---|---|---|
| **Requires Root?** | No | Yes |
| **Output File** | `.apk` file | `.zip` Magisk module |
| **How to Install** | Standard APK install | Flash in Magisk / KernelSU app |
| **Package Name** | `com.google.android.dialer.amoled` (cloned) | `com.google.android.dialer` (original) |
| **Coexists with Stock?** | Yes, installs alongside factory dialer | Replaces factory dialer systemlessly |

---

## Getting Started

### 1. Download a Stock Google Phone APK
Download any official Google Phone APK from [APKMirror](https://www.apkmirror.com/apk/google-inc/google-phone/) (pick `arm64-v8a` or `armeabi-v7a` matching your device).

### 2. Clone This Repository
Open your terminal / command prompt and run:
```bash
git clone https://github.com/Gagan-Pratap-S/google-dialer-amoled.git
cd google-dialer-amoled
```

---

## How to Build

Choose whichever method fits your workflow:

### Method A: Using an AI Coding Assistant (Easiest)

You can run this project through any AI coding assistant—**Google Antigravity**, **Cursor**, **Claude Code**, or **GitHub Copilot**.

1. **If using Antigravity**:
   Copy this folder into your skills directory:
   - Windows: `C:\Users\<YourUsername>\.gemini\config\skills\google-dialer-amoled`
   - Linux/macOS: `~/.gemini/config/skills/google-dialer-amoled`
   
   **For Non-Root phones**, prompt:
   > *"Convert my Google Dialer APK at `path/to/dialer.apk` to an AMOLED build for non-root."*
   
   **For Rooted phones (Magisk / KernelSU module)**, prompt:
   > *"Build a Magisk systemless module from my Google Dialer APK at `path/to/dialer.apk` with the AMOLED theme."*

2. **If using Cursor / Claude Code / Copilot**:
   Open this repository folder in your editor or terminal, place your stock APK in the folder, and prompt the agent:
   
   **For Non-Root phones**:
   > *"Run scripts/mod_dialer.py on stock_dialer.apk to produce a non-root AMOLED APK with bumped version."*
   
   **For Rooted phones (Magisk module)**:
   > *"Run scripts/mod_dialer.py on stock_dialer.apk with `--mode system` and output a flashable Magisk module zip."*

The assistant will read `SKILL.md`, execute the Python script, and output your signed APK or flashable Magisk zip.

---

### Method B: Terminal / Command Line (Direct)

You only need **Python 3.8+** and **Java (JRE 17+)** installed on your system. The script automatically handles downloading `apktool` and `uber-apk-signer` on its first run if they are not already installed.

#### For Non-Root Phones (Standalone APK):
```bash
python scripts/mod_dialer.py build --input "path/to/stock_dialer.apk" --mode cloned
```
That's it. The script decompiles the APK, applies the AMOLED palette, clones the package name to prevent signature conflicts, bumps the `versionCode` so Android won't block it as a downgrade, rebuilds, and signs it.

Your output file will be saved right next to your input as `stock_dialer_AMOLED.apk`.

#### For Rooted Phones (Magisk Module):
```bash
python scripts/mod_dialer.py build --input "path/to/stock_dialer.apk" --mode system --magisk "GoogleDialer_AMOLED_Magisk.zip"
```

---

### Method C: Morphe Manager (Android App)

If you use **Morphe Manager** or **ReVanced Manager** directly on your phone:
- Check the [`morphe/`](morphe/) directory in this repository.
- Contains native Kotlin patches (`GoogleDialerThemePatch.kt` and `GoogleDialerClonePatch.kt`) ready to be bundled or submitted directly to [MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches).
- See [`morphe/README.md`](morphe/README.md) for instructions on building or loading custom patch bundles into Morphe Manager.

---

## Installing on Your Phone

- **Non-Root**: Copy the generated `_AMOLED.apk` to your phone and tap it in your file manager to install (or run `adb install -r path/to/app.apk`). Once installed, open your phone's **Default Apps** settings and set **Phone (AMOLED)** as your default calling app.
- **Rooted**: Copy the `.zip` file to your phone, open the **Magisk** or **KernelSU** app, head to **Modules → Install from storage**, select the zip, and reboot.

---

## Troubleshooting

- **`INSTALL_FAILED_UPDATE_INCOMPATIBLE`**: Happens when trying to install an APK with the official package name over the factory-installed dialer without Google's private key. Always use `--mode cloned` on non-rooted phones.
- **`INSTALL_FAILED_VERSION_DOWNGRADE`**: The script bumps `versionCode` to `20999999` by default. If you manually disable it with `--no-bump-version` and see this error, re-run with version bumping enabled.
- **Missing Java error**: Make sure Java JRE 17+ is installed and accessible from your terminal (`java -version`).

---

## Repository Structure

```
google-dialer-amoled/
├── SKILL.md                      # Agent skill instructions (Antigravity / Cursor / Claude)
├── README.md                     # Documentation and usage guide
├── morphe/                       # Morphe / ReVanced Manager Kotlin patch source
│   ├── GoogleDialerThemePatch.kt
│   ├── GoogleDialerClonePatch.kt
│   └── README.md
├── references/
│   └── amoled_palette.json       # Color definitions & silhouette outline mappings
└── scripts/
    └── mod_dialer.py             # Main CLI build & patching script
```

---

## License

This project is licensed under the [MIT License](LICENSE).

*Disclaimer: This repository does not contain or distribute any Google proprietary binaries or APK files. Users supply their own legally acquired APKs.*
