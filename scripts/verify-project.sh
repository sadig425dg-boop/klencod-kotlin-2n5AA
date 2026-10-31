#!/bin/bash
# 2n5a Project Structure Verification Script
# Validates both Web and Android project structures

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

ERRORS=0
WARNINGS=0

pass() { echo -e "${GREEN}✅ $1${NC}"; }
fail() { echo -e "${RED}❌ $1${NC}"; ERRORS=$((ERRORS + 1)); }
warn() { echo -e "${YELLOW}⚠️  $1${NC}"; WARNINGS=$((WARNINGS + 1)); }

# Determine project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

echo "=========================================="
echo "  2n5a Project Structure Verification"
echo "=========================================="
echo "Project root: $PROJECT_ROOT"
echo ""

# === Web Project ===
echo "--- Web Project (Next.js) ---"
for f in package.json tsconfig.json next.config.ts; do
    [ -f "$f" ] && pass "Web: $f" || fail "Web: $f missing"
done
[ -d "src/app" ] && pass "Web: src/app/ directory" || fail "Web: src/app/ missing"
[ -f "src/app/page.tsx" ] && pass "Web: src/app/page.tsx" || fail "Web: src/app/page.tsx missing"
[ -f "src/db/schema.ts" ] && pass "Web: src/db/schema.ts" || fail "Web: src/db/schema.ts missing"
echo ""

# === Android Project ===
echo "--- Android Project ---"
ANDROID_DIR="$PROJECT_ROOT/android"
if [ ! -d "$ANDROID_DIR" ]; then
    fail "Android directory not found at $ANDROID_DIR"
else
    cd "$ANDROID_DIR"
    
    # Core Gradle files
    for f in settings.gradle.kts build.gradle.kts gradle.properties gradlew gradlew.bat; do
        [ -f "$f" ] && pass "Android: $f" || fail "Android: $f missing"
    done
    
    # Gradle wrapper
    [ -f "gradle/wrapper/gradle-wrapper.properties" ] && pass "Android: gradle-wrapper.properties" || fail "Android: gradle-wrapper.properties missing"
    
    # App module
    [ -f "app/build.gradle.kts" ] && pass "Android: app/build.gradle.kts" || fail "Android: app/build.gradle.kts missing"
    
    # AndroidManifest
    [ -f "app/src/main/AndroidManifest.xml" ] && pass "Android: AndroidManifest.xml" || fail "Android: AndroidManifest.xml missing"
    
    # Kotlin sources
    KOTLIN_COUNT=$(find app/src/main -name "*.kt" 2>/dev/null | wc -l)
    [ "$KOTLIN_COUNT" -gt 0 ] && pass "Android: $KOTLIN_COUNT Kotlin files found" || fail "Android: No Kotlin source files"
    
    # Resources
    [ -f "app/src/main/res/values/strings.xml" ] && pass "Android: strings.xml" || fail "Android: strings.xml missing"
    [ -f "app/src/main/res/values-ar/strings.xml" ] && pass "Android: Arabic strings.xml" || fail "Android: Arabic strings.xml missing"
    [ -f "app/src/main/res/values/themes.xml" ] && pass "Android: themes.xml" || fail "Android: themes.xml missing"
    
    # Gradlew executable
    [ -x "gradlew" ] && pass "Android: gradlew is executable" || warn "Android: gradlew is not executable (run chmod +x)"
    
    # Check settings.gradle.kts content
    if grep -q 'include(":app")' settings.gradle.kts 2>/dev/null; then
        pass "Android: settings.gradle.kts includes :app module"
    else
        fail "Android: settings.gradle.kts does not include :app module"
    fi
    
    # Check rootProject.name
    if grep -q 'rootProject.name' settings.gradle.kts 2>/dev/null; then
        pass "Android: rootProject.name is set"
    else
        warn "Android: rootProject.name not found in settings.gradle.kts"
    fi
    
    cd "$PROJECT_ROOT"
fi
echo ""

# === GitHub Actions ===
echo "--- GitHub Actions ---"
[ -f ".github/workflows/android-build.yml" ] && pass "CI: android-build.yml" || fail "CI: android-build.yml missing"
if [ -f ".github/workflows/android-build.yml" ]; then
    if grep -q "working-directory: android" .github/workflows/android-build.yml 2>/dev/null; then
        pass "CI: working-directory set to android"
    else
        fail "CI: working-directory not set to android"
    fi
fi
echo ""

# === Conflict Detection ===
echo "--- Conflict Detection ---"
# Check no Gradle files in web root
if [ -f "settings.gradle.kts" ] || [ -f "settings.gradle" ]; then
    fail "Conflict: Gradle settings file found in web root (will confuse Gradle)"
else
    pass "No Gradle files in web root"
fi

# Check no competing android roots
SETTINGS_COUNT=$(find . -name "settings.gradle.kts" -o -name "settings.gradle" 2>/dev/null | grep -v node_modules | grep -v .next | wc -l)
if [ "$SETTINGS_COUNT" -gt 1 ]; then
    warn "Multiple Gradle settings files detected ($SETTINGS_COUNT) - potential conflict"
    find . -name "settings.gradle.kts" -o -name "settings.gradle" 2>/dev/null | grep -v node_modules | grep -v .next
else
    pass "Single Gradle root detected"
fi
echo ""

# === Summary ===
echo "=========================================="
echo "  Verification Summary"
echo "=========================================="
echo -e "Errors:   ${RED}$ERRORS${NC}"
echo -e "Warnings: ${YELLOW}$WARNINGS${NC}"
echo ""
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ Project structure is valid!${NC}"
    exit 0
else
    echo -e "${RED}❌ Project has $ERRORS error(s). Fix them before building.${NC}"
    exit 1
fi
