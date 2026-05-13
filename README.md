# Grama-Urja — Android (Kotlin + Jetpack Compose)

This is the **native Android version** of the Grama-Urja web app, written from
scratch in **Kotlin + Jetpack Compose**. It connects to the same Lovable Cloud
(Supabase) backend as the web app — same users, same zones, same data.

## What's included

- All 6 screens: **Auth, Home, Zones, Pump Timer, Settings, Admin**
- **Supabase Kotlin SDK** for auth, database, realtime
- **Background notifications** via a foreground service (`PowerWatchService`)
  that keeps a Realtime channel open and posts an OS notification when the
  power status changes — works even when the app is closed (as long as the
  phone has internet)
- **3 languages**: English, Hindi, Kannada
- Material 3 theme matching the green palette of the web app

## How to open in Android Studio

1. **Install Android Studio Hedgehog (2023.1.1) or newer.**
2. Open Android Studio → **File → Open** → select this `grama-urja-android`
   folder.
3. Android Studio will prompt to install the Android Gradle Plugin and SDKs
   it needs (compileSdk 34, JDK 17). Accept all prompts.
4. When asked to **generate the Gradle wrapper**, click yes — or open a
   terminal in the project folder and run `gradle wrapper` (requires Gradle
   8.7 installed locally). The wrapper download script is intentionally not
   included to keep the zip small.
5. Wait for **Gradle sync** to finish (5–10 minutes the first time, it
   downloads dependencies).
6. Connect an Android phone (USB debugging on) **or** create an emulator.
7. Click ▶️ **Run**.

## Login

Use the **same email and password** you use on the web app. Your account,
zone, and admin role all carry over automatically — same backend.

## How background notifications work

When you open the app, it starts a **foreground service** that subscribes to
your zone's Realtime channel. Whenever an admin reports power ON/OFF, the
service receives the change and posts a system notification — even if the
app is in the background or the screen is off.

A small persistent notification ("Listening for power updates…") indicates
the service is running. This is required by Android's foreground service
rules; you can dismiss it from notification settings if you want.

**Limitation**: the phone needs internet for this to work. There is no
"true offline" notification — that would require Firebase Cloud Messaging
(FCM) and a push server.

## Build a release APK

In Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
The APK appears in `app/build/outputs/apk/`.

## Project structure

```
app/src/main/java/com/gramaurja/app/
├── App.kt                  Application class, notification channels
├── MainActivity.kt         Entry point, starts foreground service
├── data/
│   ├── Supa.kt             Supabase client
│   ├── LocalPrefs.kt       SharedPreferences for lang + watched zone
│   ├── model/Models.kt     Zone, Profile, StatusUpdate, Crop
│   └── repo/Repo.kt        All database calls
├── service/
│   └── PowerWatchService   Foreground Realtime listener + notifier
├── ui/
│   ├── AppRoot.kt          Auth gate + bottom nav
│   ├── nav/Dest.kt         Navigation destinations
│   ├── theme/Theme.kt      Material 3 theme
│   └── screens/            Auth, Home, Zones, Pump, Settings, Admin
└── util/I18n.kt            English / Hindi / Kannada strings
```

## For your college report

Tech stack to mention:
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Single-activity, Compose Navigation, Repository pattern
- **Backend**: Supabase (PostgreSQL + Auth + Realtime), connected via the
  official `supabase-kt` SDK
- **Background work**: Android Foreground Service + Coroutines + Realtime
  WebSocket subscription
- **Notifications**: `NotificationManager` with high-importance channel
- **Min SDK**: 24 (Android 7.0); **Target SDK**: 34 (Android 14)
