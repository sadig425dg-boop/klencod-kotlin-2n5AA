#!/bin/bash
# 2n5a Android Build Script
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_DIR="$SCRIPT_DIR/../android"

echo "=========================================="
echo "  2n5a Android Build"
echo "=========================================="

cd "$ANDROID_DIR"
echo "Working directory: $(pwd)"

# Validate structure
if [ ! -f "settings.gradle.kts" ]; then
    echo "❌ ERROR: settings.gradle.kts not found in $(pwd)"
    echo "   Make sure you're running from the project root."
    exit 1
fi

chmod +x gradlew

echo ""
echo "--- Gradle Version ---"
./gradlew --version

echo ""
echo "--- Clean ---"
./gradlew clean

echo ""
echo "--- Build Debug APK ---"
./gradlew assembleDebug --stacktrace

echo ""
echo "--- Verify APK ---"
APK="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK" ]; then
    echo "✅ APK built successfully!"
    echo "   Path: $ANDROID_DIR/$APK"
    echo "   Size: $(du -h "$APK" | cut -f1)"
else
    echo "❌ APK not found!"
    find app/build -name "*.apk" 2>/dev/null || echo "No APK files found"
    exit 1
fi

echo ""
echo "=========================================="
echo "  Build Complete"
echo "=========================================="
