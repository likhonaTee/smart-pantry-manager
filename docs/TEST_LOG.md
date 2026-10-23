# Test log

Use the actual execution date. Identify the requirement, expected result, observed result and pass/fail. Record build, test and lint outcomes with the toolchain when Android exists.

The entries below record each development milestone in order. Initial results refer to the screen-only foundation; later entries cover the database and pantry features. See FRS.md section 5 for the full planned feature acceptance checks. Build-machine tests are distinguished from emulator evidence.

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

The initial landing screen is not completion of FR01. Later implementation checks are recorded below.

## Database milestone

The user's 23 October screenshot shows the app installed and its landing screen displayed in Android Studio's Pixel 8 Pro emulator. Rotation, font scaling and data entry were not shown.

Database tests use JUnit 4.13.2 and Robolectric 4.14.1 with Android API 28. These are build-machine tests, not physical-device evidence. Full matching, settings and navigation checks remain pending.

| Date | Check | Expected result | Observed result | Status |
| --- | --- | --- | --- | --- |
| 2026-10-23 | DatabaseHelperTest | Pantry CRUD persists; recipes seed once; IDs and quoted input handled safely | 6 tests passed with no failures or skipped tests | Pass |
| 2026-10-23 | Database milestone build and lint | Debug APK builds and lint passes | scripts/check.sh passed; lint reports No issues found | Pass |

## Pantry list milestone

| Date | Check | Expected result | Observed result | Status |
| --- | --- | --- | --- | --- |
| 2026-10-23 | PantryActivityTest | Empty state shown; adapter displays saved data and refreshes on resume | 2 Activity tests passed alongside 6 database tests | Pass |
| 2026-10-23 | Pantry list build and lint | Build and all current tests pass | scripts/check.sh passed; lint reports No issues found | Pass |

The list tests use Robolectric. The updated screen still needs visual review on the user's emulator, including rotation and large system fonts.

## GitHub SDK setup correction

The GitHub run for 5b0cc4a stopped at SDK setup before app checks because setup-android requested the removed package named tools. Earlier Android runs had the same setup failure. The workflow now requests only platform-tools, platforms;android-35 and build-tools;34.0.0. The next GitHub run will verify this correction; local app checks passed separately.

## Ingredient form milestone

| Date | Check | Expected result | Observed result | Status |
| --- | --- | --- | --- | --- |
| 2026-10-23 | Name and quantity rules | Aliases and decimals normalized; invalid amounts rejected | 3 IngredientUtils tests passed | Pass |
| 2026-10-23 | Add form validation and recreation | Valid data saved; invalid data refused; unsaved values restored | 3 IngredientActivity tests passed | Pass |
| 2026-10-23 | Full form milestone checks | Build all tests and lint pass | scripts/check.sh passed; 14 total tests passed; lint reports No issues found | Pass |

Name cleaning is introduced with data entry so saved rows already use canonical keys. Unit conversions remain planned with recipe matching. The updated form still needs user emulator review.

## Editing and deletion milestone

| Date | Check | Expected result | Observed result | Status |
| --- | --- | --- | --- | --- |
| 2026-10-23 | Edit recreation delete confirmation and missing IDs | Existing ID updated; cancellation preserves stock; confirmed delete removes it; missing ID returns safely | All 6 IngredientActivity tests passed | Pass |
| 2026-10-23 | Pantry Intents and refresh | Add has no edit ID; row selection passes the correct ID; list refreshes | All 4 PantryActivity tests passed | Pass |
| 2026-10-23 | Full pantry checks | Debug build all tests and lint pass | scripts/check.sh passed; 19 tests passed; lint reports No issues found | Pass |
| 2026-10-23 | GitHub SDK fix and add form runs | Cloud workflow completes | Runs 39949489073 and 39949621651 completed successfully | Pass |
| 2026-10-23 | Updated pantry and form on user emulator | Add edit cancel delete restart rotation and large fonts work visually | User test still required after pulling latest changes | Not run |

The initial deletion-confirmation test checked the row before Android processed the dialog callback. Waiting for the main looper in that test resolved the failure; the final test run passed. These host tests do not replace the user's on-device evidence for the report.
