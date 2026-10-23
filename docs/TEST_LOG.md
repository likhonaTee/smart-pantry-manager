# Test log

Use the actual execution date. Identify the requirement, expected result, observed result and pass/fail. Record build, test and lint outcomes with the toolchain when Android exists.

The Android foundation exists with one Java Activity and an XML pantry landing screen. No behavioural unit tests or device tests exist yet. See FRS.md section 5 for the planned feature acceptance checks. Rows below preserve earlier results and later verification separately.

| Date | Check | Expected result | Observed result | Status |
| --- | --- | --- | --- | --- |
| 2026-10-23 | Planning documents and script | Required documents present; no whitespace errors | bash scripts/check.sh passed; bash -n accepted the check script | Pass |
| 2026-10-23 | FRS document layout | Readable pages without clipping or overlap | Nine rendered pages visually reviewed | Pass |
| 2026-10-23 | Android build tests and lint | Run once Android project exists | No Android project or Gradle wrapper exists | Not run |
| 2026-10-23 | GitHub Actions execution | Run after push to GitHub | No connected GitHub remote yet | Not run |

## Android foundation environment

Build environment: Linux, OpenJDK 17.0.20, Gradle 8.9, Android Gradle Plugin 8.7.3, Android SDK Platform 35 and Build Tools 34.0.0. Android Studio and an emulator were not available in this environment. Setup instructions for Android Studio are in README.md.

During setup, the SDK archive extracted the platform files into an extra directory and the available Java runtime lacked jlink. The SDK directory was corrected and a checksum-verified complete JDK was extracted into scratch tooling. These are environment fixes; the project contains no machine-specific SDK paths or proxy settings.

| Date | Check | Expected result | Observed result | Status |
| --- | --- | --- | --- | --- |
| 2026-10-23 | Gradle distribution and wrapper | Official SHA256 values match | Gradle 8.9 ZIP and wrapper JAR verified | Pass |
| 2026-10-23 | Source XML and APK metadata | XML parses; launcher and SDKs correct | XML parsed; APK identifies PantryActivity, min SDK 26 and target SDK 35 | Pass |
| 2026-10-23 | scripts/check.sh and assembleDebug | Documentation whitespace and compilation pass | Final full script succeeded and debug APK was generated | Pass |
| 2026-10-23 | lintDebug | No lint errors | Final lint report states No issues found | Pass |
| 2026-10-23 | testDebugUnitTest | Run available Java tests | NO-SOURCE because this milestone has no test classes | Not applicable |
| 2026-10-23 | Device launch layout rotation and font scaling | Screen remains readable | No emulator or connected device available | Not run |

Full pantry CRUD, persistence, recipe matching, seed, settings and navigation tests remain pending implementation. The initial landing screen is not completion of FR01.
