# Grama-Urja — Android (Kotlin + Jetpack Compose)

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
7. Click  **Run**.

## Login

Admin mail: Sharmarithika21@gmail.com
Password:Minty@042(dummy password)


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
