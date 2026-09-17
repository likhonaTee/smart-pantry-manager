# Smart Pantry Manager

A planned Java Android app that tracks pantry ingredients and suggests recipes only when every required ingredient is available in enough quantity.

## Current state

The Java Android foundation now includes SQLite tables, pantry database operations and 20 starter recipes. The pantry form and database-backed list are the next steps; recipe matching and the other screens are not implemented yet. The repository is https://github.com/likhonaTee/smart-pantry-manager. Read docs/FRS.md before building; see docs/TEST_LOG.md for actual verification results.

## Stack

Android Studio, Java, XML layouts, five Activities, a custom ListView adapter, SQLiteOpenHelper and SharedPreferences. SQLite keeps the app local and avoids backend setup. Exactly 20 recipes will be seeded.

## Development workflow

Read AGENTS.md, docs/FRS.md and docs/DEVELOPMENT_PLAN.md. Work on one feature, run relevant checks, record actual results, inspect the diff, then commit and push. Use clear messages and actual dates.

Run the starter gate with:

```bash
bash scripts/check.sh
```

This checks required documents and whitespace, then builds the debug APK, runs Java tests and runs Android lint. Database tests use Robolectric on a simulated Android API 28 runtime to exercise SQLite on the build machine. JUnit and Robolectric are test-only dependencies and are not included in the app. Emulator checks are separate.

## Setup and run

1. Clone this repository through Android Studio using Get from VCS, or download and extract the source ZIP from GitHub.
2. Open the repository root containing settings.gradle. Do not create a second Android project inside it.
3. In SDK Manager, install Android SDK Platform 35 and SDK Build Tools 34.0.0.
4. Select JDK 17 for Gradle in Android Studio settings. Allow Gradle sync to download dependencies.
5. Create an emulator using API 26 or higher, or connect an Android 8.0 or newer device with USB debugging enabled.
6. Select the app run configuration and press Run. The Smart Pantry landing screen should open.

Android Studio writes local.properties for your local SDK path; keep this file out of Git. Android Studio itself has not been run in the build environment. The project uses Android Gradle Plugin 8.7.3, Gradle 8.9, JDK 17, compile/target SDK 35 and minimum SDK 26. Use an Android Studio release supporting AGP 8.7.3.

From the root, build and check using Git Bash, macOS or Linux:

```bash
bash gradlew assembleDebug testDebugUnitTest lintDebug
```

On Windows Command Prompt or PowerShell, use gradlew.bat instead of bash gradlew. The debug APK is written to app/build/outputs/apk/debug/app-debug.apk. GitHub Actions also uploads that APK when the build succeeds. Debug builds are for development and testing.

## First device check

Launch the app and confirm that the title, introduction and empty pantry message are legible. Rotate the device, use a larger system font and check that the page can scroll without hiding text. These checks still need an actual emulator or device; do not treat a successful build as proof that they passed.

## Toolchain reference

Google (n.d.) Android Gradle Plugin 8.7 release notes. https://developer.android.com/build/releases/agp-8-7-0-release-notes (Accessed 23 October 2026).

Google (n.d.) SQLiteOpenHelper. https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper (Accessed 23 October 2026).

Robolectric (n.d.) Getting started. https://robolectric.org/getting-started/ (Accessed 23 October 2026).
