#!/usr/bin/env bash
set -eu
cd "$(dirname "$0")/.."

for required_file in README.md AGENTS.md docs/FRS.md docs/DEVELOPMENT_PLAN.md docs/TEST_LOG.md; do
  test -s "$required_file" || { echo "Missing required file: $required_file"; exit 1; }
done
git diff --check
git diff --cached --check
echo "Documentation and whitespace checks passed."

if test -f gradlew; then
  test -s gradle/wrapper/gradle-wrapper.jar
  test -s gradle/wrapper/gradle-wrapper.properties
  bash gradlew --no-daemon assembleDebug testDebugUnitTest lintDebug
else
  echo "Android checks NOT RUN: Android project has not been created."
fi
