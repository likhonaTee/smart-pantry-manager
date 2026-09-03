# Smart Pantry Manager

A planned Java Android app that tracks pantry ingredients and suggests recipes only when every required ingredient is available in enough quantity.

## Current state

Planning baseline only. No Android source, Gradle wrapper or working APK exists yet. The repository is https://github.com/likhonaTee/smart-pantry-manager. The FRS is in docs/FRS.md. Read it before building.

## Planned stack

Android Studio, Java, XML layouts, five Activities, a custom ListView adapter, SQLiteOpenHelper and SharedPreferences. SQLite keeps the app local and avoids backend setup. Exactly 20 recipes will be seeded.

## Development workflow

Read AGENTS.md, docs/FRS.md and docs/DEVELOPMENT_PLAN.md. Work on one feature, run relevant checks, record actual results, inspect the diff, then commit and push. Use clear messages and actual dates.

Run the starter gate with:

```bash
bash scripts/check.sh
```

Until the Android project exists, this checks documentation and whitespace only. It does not validate an Android application.

## Setup and run

Pending implementation. Clone this repository, then create an Android Studio Java project at this repository root without replacing existing documentation. Record the tested Android Studio, SDK, JDK, Gradle and Android plugin versions here, and align .java-version with the chosen toolchain.

Once the Gradle wrapper and app exist, open this folder in Android Studio, sync Gradle, choose an emulator or device and run app. Verification commands will be:

```bash
bash gradlew assembleDebug testDebugUnitTest lintDebug
```

These commands have not run yet. Device steps and exact tested versions will be added after the project is created.
