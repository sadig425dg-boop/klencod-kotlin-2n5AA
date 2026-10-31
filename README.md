# 2n5a — تطبيق VPN عربي احترافي

تطبيق VPN عربي متكامل مبني على V2Ray/Xray مع دعم Shizuku وHotspot.

## بنية المشروع

```
2n5a/
├── android/          ← مشروع Android Native (Kotlin + Jetpack Compose)
│   ├── settings.gradle.kts
│   ├── build.gradle.kts
│   ├── gradle.properties
│   ├── gradlew / gradlew.bat
│   ├── gradle/wrapper/
│   └── app/
│       ├── build.gradle.kts
│       └── src/main/
│           ├── AndroidManifest.xml
│           ├── java/com/n2a5/app/
│           └── res/
│
├── src/              ← مشروع الويب (Next.js + TypeScript + Drizzle)
│   ├── app/
│   ├── components/
│   ├── db/
│   └── lib/
│
├── scripts/          ← سكربتات الفحص والبناء
├── .github/workflows/← GitHub Actions CI/CD
├── package.json      ← إعدادات مشروع الويب
└── README.md
```

## بناء تطبيق Android

### المتطلبات
- JDK 17+
- Android SDK (compileSdk 35)
- Android Build Tools

### البناء من سطر الأوامر
```bash
cd android
chmod +x gradlew
./gradlew assembleDebug
```

APK الناتج: `android/app/build/outputs/apk/debug/app-debug.apk`

### البناء بـ Android Studio
1. افتح مجلد `android/` كمشروع
2. انتظر Gradle Sync
3. اختر Build → Build Bundle(s) / APK(s) → Build APK(s)

### GitHub Actions
البناء يتم تلقائياً عند push إلى `main` أو `develop` عبر:
`.github/workflows/android-build.yml`

## تشغيل مشروع الويب

```bash
npm install
npm run dev
```

## السكربتات

| السكربت | الوظيفة |
|---------|---------|
| `scripts/verify-project.sh` | فحص بنية المشروع |
| `scripts/doctor.sh` | فحص بيئة البناء |
| `scripts/build-android.sh` | بناء APK |
| `scripts/verify-apk.sh` | فحص APK الناتج |

## الميزات
- دعم VLESS / VMess / Trojan / Shadowsocks
- واجهة عربية احترافية مع RTL
- استيراد إعدادات من روابط وملفات
- إدارة السيرفرات والاشتراكات
- مشاركة VPN عبر Hotspot (Shizuku)
- تصميم Dark Premium (Pink/Purple)
- إحصائيات وسجلات مفصلة

## الترخيص
MIT
