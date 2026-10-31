#!/bin/bash
# 2n5a Doctor Script - Check build environment prerequisites

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=========================================="
echo "  2n5a Environment Doctor"
echo "=========================================="
echo ""

check() {
    local name="$1"
    local cmd="$2"
    if command -v "$cmd" &>/dev/null; then
        local ver
        ver=$($3 2>&1 | head -1)
        echo -e "${GREEN}✅ $name${NC}: $ver"
    else
        echo -e "${RED}❌ $name${NC}: not found"
    fi
}

echo "--- Core Tools ---"
check "Java" "java" "java -version"
check "Kotlin" "kotlin" "kotlin -version"
check "Gradle" "gradle" "gradle --version"
check "Node.js" "node" "node --version"
check "npm" "npm" "npm --version"
check "Git" "git" "git --version"
echo ""

echo "--- Android SDK ---"
if [ -n "$ANDROID_HOME" ]; then
    echo -e "${GREEN}✅ ANDROID_HOME${NC}: $ANDROID_HOME"
    if [ -d "$ANDROID_HOME/build-tools" ]; then
        BT=$(ls "$ANDROID_HOME/build-tools/" 2>/dev/null | sort -V | tail -1)
        echo -e "${GREEN}  Build Tools${NC}: $BT"
    fi
    if [ -d "$ANDROID_HOME/platforms" ]; then
        PLT=$(ls "$ANDROID_HOME/platforms/" 2>/dev/null | sort -V | tail -1)
        echo -e "${GREEN}  Platform${NC}: $PLT"
    fi
elif [ -n "$ANDROID_SDK_ROOT" ]; then
    echo -e "${GREEN}✅ ANDROID_SDK_ROOT${NC}: $ANDROID_SDK_ROOT"
else
    echo -e "${RED}❌ ANDROID_HOME/ANDROID_SDK_ROOT${NC}: not set"
fi

if command -v adb &>/dev/null; then
    echo -e "${GREEN}✅ adb${NC}: $(adb version 2>&1 | head -1)"
else
    echo -e "${YELLOW}⚠️  adb${NC}: not found"
fi

if command -v aapt2 &>/dev/null; then
    echo -e "${GREEN}✅ aapt2${NC}: available"
else
    echo -e "${YELLOW}⚠️  aapt2${NC}: not found"
fi
echo ""

echo "--- Optional Tools ---"
check "Go" "go" "go version"
check "NDK (ndk-build)" "ndk-build" "ndk-build --version"
check "CMake" "cmake" "cmake --version"
echo ""

echo "--- Project Gradle Wrapper ---"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_DIR="$SCRIPT_DIR/../android"
if [ -f "$ANDROID_DIR/gradle/wrapper/gradle-wrapper.properties" ]; then
    GRADLE_VER=$(grep distributionUrl "$ANDROID_DIR/gradle/wrapper/gradle-wrapper.properties" | sed 's/.*gradle-//' | sed 's/-bin.*//')
    echo -e "${GREEN}✅ Gradle Wrapper${NC}: $GRADLE_VER"
else
    echo -e "${RED}❌ Gradle Wrapper${NC}: not found"
fi
echo ""

echo "--- Build Compatibility Matrix ---"
echo -e "${BLUE}Environment       | Android Build | Notes${NC}"
echo "Android Studio    | ✅ Full       | Recommended"
echo "GitHub Actions    | ✅ Full       | CI/CD ready"
echo "Linux + JDK 17    | ✅ Full       | Command line"
echo "macOS + JDK 17    | ✅ Full       | Command line"
echo "Windows + JDK 17  | ✅ Full       | Command line"
echo "Termux            | ⚠️  Limited   | No Android SDK by default"
echo "KlencoIDE         | ❌ None       | Does not support Gradle/Kotlin"
echo "WZH 2 APK         | ❌ None       | Not compatible with native projects"
echo ""
echo "=========================================="
