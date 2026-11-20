# Google Dialer AMOLED Theme Modder & Skill 🎨🖤

Convert any stock **Google Phone (Google Dialer)** app into a true, battery-saving **pure pitch-black (`#000000`) AMOLED dark theme**.

Unlike Google's default Material You dark grey (`#1f1f1f`), this mod turns all surfaces into true pitch black to maximize OLED/AMOLED battery savings, improves outdoor text legibility, and fixes missing contact avatars.

---

## ✨ Features at a Glance

- **100% True AMOLED Black (`#000000`)**: Saves battery on OLED/AMOLED displays and looks deeply contrasty.
- **Crystal Clear Text**: Dialpad digits, letters, and empty-state text are tinted pure white (`#ffffffff`) so they are easy to read under direct sunlight.
- **Silhouette Avatar Fix**: Restores visible outlines for unsaved numbers and customer care contacts instead of disappearing into black.
- **Stock Navigation & Layout Preserved**: Does not break or alter Google's clean 2-tab bottom dock (Favorites & Keypad).
- **Zero Broken Features**: Only modifies visual colors and styles. No code or logic is changed.
- **Supports Both Non-Root and Rooted Phones**:
  - **Non-Root**: Installs side-by-side as a companion app without uninstalling your stock dialer.
  - **Rooted (Magisk / KernelSU / APatch)**: Installs cleanly as a systemless module over the system app.

---

## 📱 Root vs. Non-Root (Which one should I choose?)

| | Non-Root (Most Users) | Rooted (Advanced / Magisk) |
|---|---|---|
| **Requires Root?** | ❌ No | ✅ Yes (Magisk, KernelSU, or APatch) |
| **Output File** | `.apk` file | `.zip` Magisk module |
| **How to Install** | Tap to install or via ADB | Flash in Magisk / KernelSU app |
| **Replaces System App?** | No, installs alongside stock dialer (`com.google.android.dialer.amoled`) | Yes, mounts systemlessly over `/product/priv-app/GoogleDialer/` |
| **AMOLED Black Theme** | ✅ 100% Working | ✅ 100% Working |
| **High Contrast Text** | ✅ 100% Working | ✅ 100% Working |
| **Silhouette Avatar Fix** | ✅ 100% Working | ✅ 100% Working |

---

## 🔰 Beginner's Guide: How to Use (Step-by-Step)

You have two simple ways to use this project:
1. **Method 1**: Using **Antigravity AI** (Easiest — the AI does everything for you).
2. **Method 2**: Using the **Terminal / Command Prompt** (Simple 1-command build).

---

### Method 1: Using Antigravity AI (Easiest)

If you are using Google's Antigravity agentic assistant:

1. **Copy the Skill Folder**:
   Copy this repository folder into your local Antigravity skills directory:
   - **Windows**: `C:\Users\<YourUsername>\.gemini\config\skills\google-dialer-amoled`
   - **Linux / macOS**: `~/.gemini/config/skills/google-dialer-amoled`
2. **Download a Stock Google Dialer APK**:
   Download any official Google Phone APK from a trusted source like [APKMirror](https://www.apkmirror.com/apk/google-inc/google-phone/) (choose your architecture, typically `arm64-v8a` or `armeabi-v7a`).
3. **Ask Antigravity in Chat**:
   Simply say:
   > *"Convert my Google Dialer APK at `C:\path\to\GoogleDialer.apk` into an AMOLED build for my non-root phone."*
4. **Done!** Antigravity will automatically execute the skill, apply the palette, sign the APK, and hand you the finished installation file.

---

### Method 2: Manual Terminal Build (For Non-Techies)

If you prefer running it yourself without AI, follow this simple walkthrough:

#### Step 1: Install the Requirements (One-Time Setup)
Make sure you have:
1. **Python 3.8+**: [Download from python.org](https://www.python.org/downloads/) (Make sure to check *"Add Python to PATH"* during install).
2. **Java (JRE 17+)**: [Download from adoptium.net](https://adoptium.net/) (Needed by Apktool).
3. **Apktool**: Download `apktool.jar` from [apktool.org](https://apktool.org/) and place it in an easy-to-find folder.
4. **Uber-APK-Signer**: Download `uber-apk-signer.jar` from [GitHub Releases](https://github.com/patrickfav/uber-apk-signer/releases) and put it in the same folder.

#### Step 2: Download a Stock Google Phone APK
- Go to [APKMirror - Google Phone](https://www.apkmirror.com/apk/google-inc/google-phone/).
- Download the version you want (e.g., `Google Phone 241.0.xxx`).
- Save it to your folder as `stock_dialer.apk`.

#### Step 3: Run the Command

Open **Command Prompt** (CMD) or **Terminal** in this project directory.

##### 👉 For Standard Non-Root Phones:
```bash
python scripts/mod_dialer.py build ^
  --input "C:\path\to\stock_dialer.apk" ^
  --output "C:\path\to\GoogleDialer_AMOLED.apk" ^
  --mode cloned ^
  --bump-version ^
  --apktool "C:\path\to\apktool.jar" ^
  --signer "C:\path\to\uber-apk-signer.jar"
```
*(On Linux or macOS, replace `^` with `\`)*

##### 👉 For Rooted Phones (Magisk Module):
```bash
python scripts/mod_dialer.py build ^
  --input "C:\path\to\stock_dialer.apk" ^
  --output "C:\path\to\GoogleDialer_System.apk" ^
  --mode system ^
  --magisk "C:\path\to\GoogleDialer_AMOLED_Magisk.zip" ^
  --apktool "C:\path\to\apktool.jar" ^
  --signer "C:\path\to\uber-apk-signer.jar"
```

#### Step 4: Install on Your Phone

- **Non-Root**: Transfer `GoogleDialer_AMOLED.apk` to your phone and tap to install (or run `adb install -r GoogleDialer_AMOLED.apk`). Open the app and set it as your default dialer!
- **Rooted**: Transfer `GoogleDialer_AMOLED_Magisk.zip` to your phone, open the **Magisk** or **KernelSU** app, go to **Modules &rarr; Install from storage**, select the `.zip`, and reboot.

---

## ❓ Frequently Asked Questions (FAQ)

### Q: Will this replace my official phone app?
- **Non-Root mode (`--mode cloned`)**: No! It installs alongside your existing Phone app under a separate package name (`com.google.android.dialer.amoled`). You can have both apps installed at the same time and switch back anytime.
- **Root mode (`--mode system`)**: Yes, it mounts over the system dialer so it cleanly replaces the grey factory app.

### Q: Why do I get `INSTALL_FAILED_UPDATE_INCOMPATIBLE`?
This error happens if you try to install an APK with the official Google package name on a non-rooted phone without Google's private signing key.  
**Fix**: Build with `--mode cloned` (default) which renames the package ID to avoid this conflict.

### Q: Why do I get `INSTALL_FAILED_VERSION_DOWNGRADE`?
Your phone already has a newer dialer update installed.  
**Fix**: Always include `--bump-version` when running the command. This automatically bumps the internal version code to `20999999` so Android accepts it without issue.

---

## ⚖️ Legal & Disclaimer

This project is an open-source modification script and style definition. It **does NOT contain or distribute any copyrighted Google proprietary binaries or APK files**. Users provide their own legally acquired APKs for theming.

Google Phone is a trademark of Google LLC. This project is independent and not affiliated with or endorsed by Google LLC.

---

## 📄 License

This project is open-source and licensed under the [MIT License](LICENSE).
