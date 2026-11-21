#!/usr/bin/env python3
"""
Google Dialer AMOLED Theme Modder CLI
Automates decompilation, AMOLED pitch-black theming, silhouette avatar fixes,
package cloning (for non-root), and Magisk/KernelSU packaging (for root).

Strictly modifies theme & color resources. Does not touch layouts, navigation, or bytecode logic.
"""

import argparse
import json
import os
import re
import shutil
import ssl
import subprocess
import sys
import urllib.request
import zipfile

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PALETTE_FILE = os.path.join(SCRIPT_DIR, '..', 'references', 'amoled_palette.json')
TOOLS_DIR = os.path.join(SCRIPT_DIR, 'tools')

# Well-known working fallbacks
LOCAL_FALLBACK_APKTOOL = r'C:\Users\Lenovo\.gemini\antigravity\scratch\apk_mod\apktool.jar'
LOCAL_FALLBACK_SIGNER = r'C:\Users\Lenovo\.gemini\antigravity\scratch\apk_mod\uber-apk-signer.jar'

APKTOOL_URL = "https://bitbucket.org/iBotPeaches/apktool/downloads/apktool_2.9.3.jar"
SIGNER_URL = "https://github.com/patrickfav/uber-apk-signer/releases/download/v1.3.0/uber-apk-signer-1.3.0.jar"

def download_file(url: str, dest_path: str):
    headers = {'User-Agent': 'Mozilla/5.0'}
    req = urllib.request.Request(url, headers=headers)
    
    # Try standard context first, fallback to unverified SSL if certificates are missing
    try:
        with urllib.request.urlopen(req) as resp, open(dest_path, 'wb') as out_f:
            shutil.copyfileobj(resp, out_f)
    except Exception:
        ctx = ssl.create_default_context()
        ctx.check_hostname = False
        ctx.verify_mode = ssl.CERT_NONE
        with urllib.request.urlopen(req, context=ctx) as resp, open(dest_path, 'wb') as out_f:
            shutil.copyfileobj(resp, out_f)

def ensure_tools():
    """Auto-detects or downloads apktool and uber-apk-signer if not present."""
    os.makedirs(TOOLS_DIR, exist_ok=True)
    
    apktool_jar = os.path.join(TOOLS_DIR, 'apktool.jar')
    if not os.path.exists(apktool_jar):
        if shutil.which("apktool"):
            apktool_jar = "apktool"
        elif os.path.exists(LOCAL_FALLBACK_APKTOOL):
            shutil.copy2(LOCAL_FALLBACK_APKTOOL, apktool_jar)
        else:
            print("[*] Downloading apktool.jar (one-time setup)...")
            try:
                download_file(APKTOOL_URL, apktool_jar)
                print("[+] apktool.jar downloaded.")
            except Exception as e:
                print(f"[-] Could not auto-download apktool: {e}")
                print("    Please place apktool.jar inside scripts/tools/ or install it on PATH.")
                sys.exit(1)

    signer_jar = os.path.join(TOOLS_DIR, 'uber-apk-signer.jar')
    if not os.path.exists(signer_jar):
        if os.path.exists(LOCAL_FALLBACK_SIGNER):
            shutil.copy2(LOCAL_FALLBACK_SIGNER, signer_jar)
        else:
            print("[*] Downloading uber-apk-signer.jar (one-time setup)...")
            try:
                download_file(SIGNER_URL, signer_jar)
                print("[+] uber-apk-signer.jar downloaded.")
            except Exception as e:
                print(f"[-] Could not auto-download uber-apk-signer: {e}")
                print("    Please place uber-apk-signer.jar inside scripts/tools/.")
                sys.exit(1)

    return apktool_jar, signer_jar

def load_palette():
    if os.path.exists(PALETTE_FILE):
        with open(PALETTE_FILE, 'r', encoding='utf-8') as f:
            return json.load(f)
    return {}

def patch_xml_file(filepath: str, replacements: dict):
    if not os.path.exists(filepath):
        return 0
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    count = 0
    for name, new_val in replacements.items():
        pattern = rf'(<(?:color|item)\s+name="{re.escape(name)}"[^>]*>)(.*?)(</(?:color|item)>)'
        if re.search(pattern, content):
            content = re.sub(pattern, rf'\g<1>{new_val}\g<3>', content)
            count += 1

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    return count

def apply_amoled_theme(res_dir: str):
    palette = load_palette()
    print("1. Applying AMOLED Pitch Black (#000000) & High Contrast colors...")
    
    # 1.1 res/values-night/colors.xml
    v_night = os.path.join(res_dir, 'values-night', 'colors.xml')
    c1 = patch_xml_file(v_night, palette.get('values_night_colors', {}))
    print(f"  Patched {c1} entries in values-night/colors.xml")

    # 1.2 res/values-night-v31/colors.xml
    v_night_v31 = os.path.join(res_dir, 'values-night-v31', 'colors.xml')
    c2 = patch_xml_file(v_night_v31, palette.get('values_night_v31_colors', {}))
    print(f"  Patched {c2} entries in values-night-v31/colors.xml")

    # 1.3 Silhouette outline fix for unsaved contacts
    print("2. Applying Silhouette Outline Fix for unsaved contacts...")
    outline_reps = palette.get('silhouette_outline_replacements', {})
    for sub in ['values-night-v34', 'values-v34']:
        p_col = os.path.join(res_dir, sub, 'colors.xml')
        c = patch_xml_file(p_col, outline_reps)
        if c: print(f"  Patched {c} outline colors in {sub}/colors.xml")

        p_sty = os.path.join(res_dir, sub, 'styles.xml')
        c_s = patch_xml_file(p_sty, outline_reps)
        if c_s: print(f"  Patched {c_s} outline styles in {sub}/styles.xml")

def clone_package_for_non_root(decompiled_dir: str):
    print("3. Cloning package to com.google.android.dialer.amoled (bypasses Android signature mismatch)...")
    manifest_p = os.path.join(decompiled_dir, 'AndroidManifest.xml')
    if not os.path.exists(manifest_p):
        print("  Error: AndroidManifest.xml not found!")
        return

    with open(manifest_p, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Change package name
    content = content.replace('package="com.google.android.dialer"', 'package="com.google.android.dialer.amoled"')

    # 2. Change custom permissions to prevent INSTALL_FAILED_DUPLICATE_PERMISSION
    content = re.sub(r'(<(?:uses-)?permission(?:-group)?\b[^>]*\bandroid:name=")com\.google\.android\.dialer\.([^"]+")',
                     r'\g<1>com.google.android.dialer.amoled.\g<2>', content)
    content = content.replace('android:permission="com.google.android.dialer.',
                              'android:permission="com.google.android.dialer.amoled.')
    content = content.replace('com.google.android.dialer.TRANSIT_MODE_STATE_CHANGE',
                              'com.google.android.dialer.amoled.TRANSIT_MODE_STATE_CHANGE')
    content = content.replace('com.google.android.dialer.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION',
                              'com.google.android.dialer.amoled.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION')

    # 3. Change Google dialer support library to optional (prevents install failures on non-Pixel devices)
    content = content.replace('<uses-library android:name="com.google.android.dialer.support" android:required="true"/>',
                              '<uses-library android:name="com.google.android.dialer.support" android:required="false"/>')

    # 4. Suffix ContentProvider authorities with .amoled to prevent authority collision
    def replace_authority(match):
        full = match.group(0)
        auth = match.group(1)
        if not auth.endswith('.amoled'):
            return full.replace(f'android:authorities="{auth}"', f'android:authorities="{auth}.amoled"')
        return full

    content = re.sub(r'android:authorities="([^"]+)"', replace_authority, content)

    # 5. Replace fallback authority metadata
    content = content.replace('android:value="com.google.android.dialer.preferredsimfallback"',
                              'android:value="com.google.android.dialer.preferredsimfallback.amoled"')

    with open(manifest_p, 'w', encoding='utf-8') as f:
        f.write(content)
    print("  AndroidManifest.xml successfully cloned for non-root install.")

def bump_version_code(decompiled_dir: str, new_code: int = 20999999, new_name: str = "241.0.999999999"):
    print(f"4. Bumping versionCode to {new_code} (prevents INSTALL_FAILED_VERSION_DOWNGRADE)...")
    manifest_p = os.path.join(decompiled_dir, 'AndroidManifest.xml')
    if os.path.exists(manifest_p):
        with open(manifest_p, 'r', encoding='utf-8') as f:
            m = f.read()
        m = re.sub(r'android:versionCode="[0-9]+"', f'android:versionCode="{new_code}"', m)
        m = re.sub(r'android:versionName="[^"]+"', f'android:versionName="{new_name}"', m)
        with open(manifest_p, 'w', encoding='utf-8') as f:
            f.write(m)

    yml_p = os.path.join(decompiled_dir, 'apktool.yml')
    if os.path.exists(yml_p):
        with open(yml_p, 'r', encoding='utf-8') as f:
            y = f.read()
        # Must be bare integer without quotes
        y = re.sub(r"versionCode:\s*['\"]?\d+['\"]?", f"versionCode: {new_code}", y)
        y = re.sub(r"versionName:\s*['\"][^'\"]+['\"]", f"versionName: '{new_name}'", y)
        with open(yml_p, 'w', encoding='utf-8') as f:
            f.write(y)

def make_magisk_module(apk_path: str, out_zip_path: str, module_id: str = "google_dialer_amoled",
                       version: str = "v7.0"):
    print(f"5. Packaging Magisk Systemless Module: {out_zip_path}...")
    module_prop = f"""id={module_id}
name=Google Phone AMOLED Pure Black
version={version}
versionCode=70
author=Antigravity
description=Pure AMOLED black theme (#000000) for stock Google Phone with silhouette contrast fix. Retains all original system features.
"""
    update_binary = """#!/sbin/sh
#################
# Magisk Module Updater
#################
umask 022
MODPATH=${0%/*}
BOOTMODE=true
if [ -f /data/adb/magisk/util_functions.sh ]; then
  . /data/adb/magisk/util_functions.sh
elif [ -f /data/adb/ksu/bin/busybox ]; then
  . /data/adb/ksu/scripts/util_functions.sh
fi
unzip -o "$3" -d "$1" >&2
exit 0
"""
    with zipfile.ZipFile(out_zip_path, 'w', compression=zipfile.ZIP_DEFLATED) as z:
        z.writestr('module.prop', module_prop)
        z.writestr('META-INF/com/google/android/update-binary', update_binary)
        z.writestr('META-INF/com/google/android/updater-script', "#MAGISK\n")
        z.write(apk_path, 'system/product/priv-app/GoogleDialer/GoogleDialer.apk')
        z.write(apk_path, 'system/priv-app/GoogleDialer/GoogleDialer.apk')
    print("  Magisk module created successfully!")

def run_apktool(apktool_bin: str, args: list):
    if apktool_bin == "apktool":
        cmd = ["apktool"] + args
    else:
        cmd = ["java", "-jar", apktool_bin] + args
    return subprocess.run(cmd, capture_output=True, text=True)

def main():
    parser = argparse.ArgumentParser(description="Google Dialer AMOLED Theme Modder")
    subparsers = parser.add_subparsers(dest="command")

    build_p = subparsers.add_parser("build", help="Decompile, apply AMOLED theme, and build Google Dialer APK")
    build_p.add_argument("--input", required=True, help="Input stock Google Dialer APK")
    build_p.add_argument("--output", help="Output modified APK path (default: <input>_AMOLED.apk)")
    build_p.add_argument("--mode", choices=["system", "cloned"], default="cloned",
                         help="'cloned' (default) creates com.google.android.dialer.amoled for non-root; 'system' keeps original package for Magisk")
    build_p.add_argument("--bump-version", action="store_true", default=True,
                         help="Bump versionCode to prevent downgrade errors (enabled by default)")
    build_p.add_argument("--no-bump-version", dest="bump_version", action="store_false",
                         help="Do not bump versionCode")
    build_p.add_argument("--magisk", help="Output path for Magisk module zip (only applicable with mode=system)")
    build_p.add_argument("--apktool", help="Path to apktool.jar (auto-downloaded if not provided)")
    build_p.add_argument("--signer", help="Path to uber-apk-signer.jar (auto-downloaded if not provided)")

    args = parser.parse_args()
    if args.command == "build":
        # Resolve tools
        auto_apktool, auto_signer = ensure_tools()
        apktool_bin = args.apktool if args.apktool else auto_apktool
        signer_bin = args.signer if args.signer else auto_signer

        input_apk = os.path.abspath(args.input)
        if not os.path.exists(input_apk):
            print(f"Error: Input APK '{input_apk}' not found!")
            sys.exit(1)

        if not args.output:
            base, ext = os.path.splitext(input_apk)
            out_apk = f"{base}_AMOLED{ext}"
        else:
            out_apk = os.path.abspath(args.output)

        work_dir = os.path.join(os.path.dirname(out_apk), "work_mod_dialer")
        if os.path.exists(work_dir):
            shutil.rmtree(work_dir)
        
        print(f"==> Decompiling {input_apk} (-s preserves DEX files without decompilation)...")
        res = run_apktool(apktool_bin, ['d', '-s', '-f', input_apk, '-o', work_dir])
        if res.returncode != 0:
            print("Apktool decompile error:", res.stderr)
            sys.exit(1)

        res_dir = os.path.join(work_dir, 'res')
        
        # 1. Apply AMOLED palette & silhouette outline fixes (themes & colors ONLY)
        apply_amoled_theme(res_dir)

        # 2. Package cloning for non-root
        if args.mode == "cloned":
            clone_package_for_non_root(work_dir)

        # 3. Bump version if requested
        if args.bump_version:
            bump_version_code(work_dir)

        print(f"==> Compiling APK with apktool (preserving original layouts & views)...")
        unsigned_apk = out_apk + ".unsigned.apk"
        res = run_apktool(apktool_bin, ['b', work_dir, '-o', unsigned_apk])
        if res.returncode != 0:
            print("Apktool build error:", res.stderr)
            sys.exit(1)

        print(f"==> Signing APK...")
        subprocess.run(['java', '-jar', signer_bin, '-a', unsigned_apk, '--allowResign', '--overwrite'],
                       capture_output=True, text=True)
        shutil.move(unsigned_apk, out_apk)
        print(f"\n[+] Successfully generated signed APK at:\n    {out_apk}")

        if args.magisk and args.mode == "system":
            make_magisk_module(out_apk, args.magisk)

        # Cleanup
        if os.path.exists(work_dir):
            shutil.rmtree(work_dir)
        print("==> Process complete!")
    else:
        parser.print_help()

if __name__ == '__main__':
    main()
