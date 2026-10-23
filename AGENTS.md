# Project instructions

Read docs/FRS.md before implementing or changing a feature. It is the project baseline. Follow the supplied assignment where it differs. Update the FRS when an authorized decision changes and explain the change.

Keep application code simple: Java, Android Studio, XML layouts, SQLiteOpenHelper, five Activities and a custom ListView adapter. Use ordinary loops and clear methods. Avoid extra frameworks and layers. Use short, useful comments and short, descriptive commit messages.

Do not implement application code until a GitHub remote is created and the planning commit has been pushed. The assignment requires GitHub from the start of development.

Work on one genuine feature at a time. Before committing, run bash scripts/check.sh, inspect git diff --check and the staged diff, and record actual relevant test results in docs/TEST_LOG.md. When Android code exists, also build, test, lint and check affected behaviour on a device. Report checks that cannot run; never call them passed.

Keep actual commit dates and accurate author attribution. Do not create fictional development history, screenshots, challenges or test results. Commit completed work only, never empty commits to satisfy a count. Keep secrets and local.properties out of Git.

Update README with tested setup instructions as the app becomes runnable. Preserve the Gradle wrapper in version control. Keep the Android project at the repository root so scripts and CI use the same paths.
