#!/bin/bash
# 2n5a APK Verification Script
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APK_PATH="${1:-$SCRIPT_DIR/../android/app/build/outputs/apk/debug/app-debug.apk}"

echo "=========================================="
echo "  2n5a APK Verification"
echo "=========================================="

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found: $APK_PATH"
    echo "Usage: $0 [path-to-apk]"
    exit 1
fi

echo "APK: $APK_PATH"
echo "Size: $(du -h "$APK_PATH" | cut -f1)"
echo ""

# Check if aapt2 is available
if command -v aapt2 &>/dev/null; then
    echo "--- APK Badge Info ---"
    aapt2 dump badging "$APK_PATH" 2>/dev/null | grep -E "^(package|application-label|sdkVersion|targetSdkVersion|native-code)" || true
    echo ""
fi

# Basic ZIP structure check
echo "--- APK Contents Summary ---"
if command -v unzip &>/dev/null; then
    echo "Files: $(unzip -l "$APK_PATH" 2>/dev/null | tail -1 | awk '{print $2}')"
    echo ""
    echo "Key entries:"
    unzip -l "$APK_PATH" 2>/dev/null | grep -E "(AndroidManifest|classes|\.so|resources)" | head -10 || true
fi

echo ""
echo "✅ APK verification complete"
