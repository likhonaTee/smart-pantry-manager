# Smart Pantry Manager Functional Requirements Specification

## Document control

Version 1.4 | 23 October 2026 | Status: implemented baseline awaiting final emulator acceptance
Module: Mobile App Development 700
Implementation status: All five screens, SQLite pantry CRUD, 20 recipe seeds, strict matching, recipe details and persisted display settings are implemented. Consult TEST_LOG.md for actual build and test results. The new recipe and settings screens still need emulator visual acceptance; final report and video evidence remain outstanding.

## Purpose

Build a small Java Android application that helps users cook with ingredients they already have. This FRS defines the required behaviour, simple technical design, acceptance checks and development workflow. Read this document before every feature and use its requirement IDs when recording progress.

## Core decision

Use Android Studio, Java, XML layouts and local SQLite through SQLiteOpenHelper. Keep the code easy for a student to read, explain and maintain. The app must suggest a recipe only when every required ingredient is present in sufficient quantity.

## Contents

1 Scope and implementation rules
2 Screens and functional requirements
3 Strict recipe matching
4 Data design and validation
5 Acceptance checks
6 Development and commit pipeline
7 Evidence and delivery

## Source and interpretation

The supplied Richfield assignment is the source of mandatory requirements. Choices such as SQLiteOpenHelper, 20 recipes and a units preference are project decisions within that brief. This specification is a planning document, not the final assignment report.

# 1 Scope and implementation rules

## User and scope

One person uses the app on one Android device. They maintain their pantry, view recipes they can make, open cooking instructions and change a display preference. No account, server, cloud sync or network connection is required for normal use.

## Technology baseline

Android Studio is the IDE. All application source code is Java, with XML for layouts and resources. Use SQLiteOpenHelper for pantry and recipe storage, SharedPreferences for the units display setting, and a ListView with a custom adapter for pantry rows. Use five Activities and explicit Intents for navigation. The foundation pins Android Gradle Plugin 8.7.3, Gradle 8.9, JDK 17, compile and target SDK 35, and minimum SDK 26. The AGP 8.7 compatibility table confirms Gradle 8.9, JDK 17 and SDK 35 support. See the official reference in README.md; actual check results belong in TEST_LOG.md.

## Simple code rules

Use clear class, method and variable names. Prefer ordinary loops, if statements and small methods. Keep one responsibility per class. Write short comments explaining a rule or a non-obvious decision. Avoid comments that merely repeat the next line. Do not add dependency injection, an MVVM framework, a repository layer, reactive streams, a backend or extra libraries unless an actual requirement needs them.

## Proposed classes

PantryActivity, IngredientActivity, SuggestionsActivity, RecipeDetailActivity and SettingsActivity handle screens. DatabaseHelper handles SQL and seeding. PantryAdapter displays pantry rows. PantryItem, Recipe and RecipeIngredient hold data. RecipeMatcher contains matching rules; IngredientUtils holds the small alias and unit conversion helpers. UnitPreferences shares the display preference and quantity formatting between Pantry and Recipe Detail. Add another class only when it clearly makes the code simpler.

## Excluded features

No Google Maps, mapping SDKs, GPS or location permission. No payments, Play Store publication, login, AI recipe generation, substitutions, shopping service or Almost There list in this version. Expiry dates and notifications are omitted because they are optional. Suggestions do not automatically consume pantry stock.

# 2 Screens and functional requirements

## Screen navigation

Pantry is the launch screen. A toolbar menu opens Pantry, Suggested Recipes or Settings. Add opens the ingredient form; selecting an existing pantry row opens that same form in edit mode. Selecting a suggestion opens Recipe Detail. Back returns to the previous screen. Intents pass database IDs rather than whole database objects.

## FR01 Pantry list

Display current ingredient name, quantity and unit using a custom ListView adapter populated from SQLite. Show a helpful message when the pantry is empty. Reload the list when returning from an add, edit or delete operation.

## FR02 Add ingredient

Provide a name field, decimal quantity field and unit selector. Save valid entries to SQLite and return to the updated pantry. Show a field error for invalid input and preserve entered values for correction.

## FR03 Edit and delete

Open the selected record by its ID. Save changes to that record only. Offer a delete action with confirmation; cancel preserves the record. Both updates and deletions must remain effective after restart.

## FR04 Recipe collection

Seed exactly 20 distinct recipes on first database creation. Each recipe has a name, numbered preparation steps and at least one required ingredient with quantity and unit. Include every ingredient, including water, oil, salt or spices when needed; do not assume free pantry staples. Seed once in a transaction and do not duplicate recipes after reopening.

## FR05 Suggestions and details

Show only recipes passing every rule in section 3. Display “No recipes match your pantry yet - add more ingredients” when none qualify. Recipe Detail displays the selected recipe name, every required ingredient and the complete method. Returning to Suggestions reruns matching against current saved pantry data.

## FR06 Settings and persistence

Settings offers a metric display preference: grams and millilitres, or kilograms and litres. Count stays count. Save the preference across restarts; it changes display only. All pantry records and seeded recipes persist after closing and reopening the app.

# 3 Strict recipe matching

## FR07 Complete match

A recipe qualifies only if every required ingredient has enough compatible pantry stock. One missing ingredient or one insufficient quantity excludes the recipe. An empty pantry returns no suggestions. A recipe with no ingredient rows is invalid and must never qualify.

## FR08 Ingredient names

Trim leading and trailing spaces, collapse repeated spaces and convert names to lower case using Locale.ROOT. Use a short explicit alias map for common forms, including tomato/tomatoes, potato/potatoes, egg/eggs and onion/onions. Do not remove the final s from every word. Use a canonical ingredient key for storage and matching. Unknown names remain valid exact canonical names; do not infer substitutions.

## FR09 Units and quantities

Supported input units are g, kg, ml, l and count. Convert kg to g by multiplying by 1000 and l to ml by multiplying by 1000. Mass, volume and count are separate dimensions. Never convert grams to millilitres or a count to mass. Cups, spoons and other ambiguous units are not supported in this version; seeded recipes must use the supported units.

## FR10 Duplicate stock

For the same canonical ingredient, add together pantry quantities within the same dimension. For example, 500 g rice plus 0.5 kg rice provides 1000 g rice. Keep separate pantry records editable by their IDs. Sum repeated recipe requirements for the same canonical ingredient and dimension too, so stock is not counted twice.

## Algorithm in plain language

Load the pantry once. Normalize names and convert quantities to base units. Build totals by ingredient key and dimension using a simple map. For each recipe, build required totals in the same way. If any total is missing or insufficient, reject that recipe; otherwise include it. Use BigDecimal from the input text for exact decimal arithmetic and comparison, avoiding floating point rounding that could allow a shortage.

## Worked example

A recipe requires 2 eggs and 200 g tomatoes. Pantry stock of 2 eggs and 0.2 kg tomato qualifies. Reducing eggs to 1 excludes it. Replacing the tomatoes with 200 ml tomato excludes it because the units are incompatible. Having salt does not replace either ingredient.

# 4 Data design and validation

## Database tables

pantry_items: id INTEGER PRIMARY KEY, name TEXT, ingredient_key TEXT, quantity TEXT, unit TEXT.
recipes: id INTEGER PRIMARY KEY, name TEXT, method TEXT.
recipe_ingredients: id INTEGER PRIMARY KEY, recipe_id INTEGER, ingredient_key TEXT, quantity TEXT, unit TEXT.
All fields except IDs are required. Store validated decimal text for exact BigDecimal arithmetic; numeric comparisons happen in Java, not by sorting text in SQL. Enable the recipe_id foreign key and keep database version 1 for the initial implementation.

## Relationships

One recipe has many recipe_ingredients. Each recipe ingredient refers to exactly one recipe. Pantry stock and recipe requirements connect through the same canonical ingredient key and unit dimension in the matcher. Pantry rows are not foreign keys to recipe rows. Keep the fixed seed IDs stable.

## FR11 Form validation

Ingredient name must contain 1 to 60 characters after whitespace normalization. Quantity must be a finite positive decimal, at most 1000000 in the selected input unit, with at most three decimal places. Reject blank, zero, negative, malformed and out-of-range quantities. Accept a single dot or comma decimal separator by normalizing comma to dot; reject mixed separators and thousands separators. Select the unit from the fixed list.

## FR12 Database safety

Use ContentValues and selection arguments for user input rather than concatenating SQL strings. Close cursors reliably. On a failed save or delete, show a clear message and keep the current record or form available. A missing ID must show a friendly message and return safely, rather than crash. Seed recipes in a transaction and keep user stock on ordinary app restarts.

## FR13 Usability and lifecycle

Use legible labels, useful button text and scrollable forms and recipe details. Preserve unsaved form values on rotation through saved instance state. Refresh pantry and suggestions in onResume where appropriate. Do not promise reminder alerts. The metric preference must affect displayed quantities without changing stored values or matching results.

## Practical limits

The first version targets a small local pantry. Use straightforward SQLite reads and measure responsiveness on the chosen emulator. If real use causes visible pauses, move the relevant database work to a simple background executor. No performance result is claimed before testing.

# 5 Acceptance checks

## AC01 Pantry operations

Add 2 eggs using count. Confirm the saved row is visible. Edit to 3 and confirm the same ID changes. Cancel deletion and verify it remains. Confirm deletion and verify it disappears. Close and reopen after each saved operation to check persistence. Covers FR01 to FR03 and FR06.

## AC02 Validation and screen flow

Try blank name, zero, negative, malformed decimal and an unsupported unit. Each must be rejected without a crash or inserted row. Check dot and comma decimals. Rotate the form with unsaved text. Open all five screens, exercise toolbar navigation and Back, and inspect the ingredient ID and recipe ID passed by Intents. Covers FR02, FR11 and FR13.

## AC03 Strict matching

Use the section 3 example. Check complete stock qualifies; one missing ingredient and one insufficient amount both fail. Check exact equality succeeds. Check an empty pantry and invalid empty recipe produce no matches. Check 0.199 kg tomato fails a 200 g requirement. Covers FR05 and FR07 to FR09.

## AC04 Names units and duplicates

Check “ TOMATOES ” and tomato match. Check 0.2 kg equals 200 g and 1 l equals 1000 ml. Check grams do not satisfy millilitres and count does not satisfy mass. Check 500 g plus 0.5 kg stock satisfies 1 kg required. Check repeated recipe rows are summed and cannot reuse insufficient stock. Covers FR08 to FR10.

## AC05 Seeds details and settings

Verify exactly 20 recipes and no duplicate seeding after restart. Inspect every recipe for quantities, supported units and useful method steps. Confirm Recipe Detail shows all stored requirements. Change the display preference and restart; confirm it persists and suggestions are unchanged. Covers FR04 to FR06 and FR13.

## Recording results

Matching and validation edge cases are covered by Java tests, alongside Robolectric database and Activity checks. Run the remaining UI, persistence and lifecycle acceptance checks on an emulator or device. Record actual date, requirement ID, expected result, observed result and pass/fail in docs/TEST_LOG.md. Attach real running-app screenshots to the final report. Consult the log for completed checks; do not infer that all emulator acceptance checks have passed.

# 6 Development and commit pipeline

## Working rule

Before each change, read docs/FRS.md and AGENTS.md. Select one small feature and its acceptance check. Build it, run relevant checks, inspect the diff, update the test log, then commit and push. Use actual commit dates. Do not split finished code into artificial historical milestones or mark planned work as complete.

## Planned incremental commits

01 Add FRS and development workflow
02 Create Java Android project
03 Add SQLite tables and recipe seeds
04 Add pantry list and adapter
05 Add ingredient form and validation
06 Add ingredient editing and deletion
07 Add name and unit helpers
08 Add strict recipe matching and tests
09 Add suggested recipes screen
10 Add recipe details screen
11 Add settings and screen navigation
12 Fix issues found during app testing
13 Add setup guide and evidence checklist

## Commit quality gate

Use short messages stating the change. Implement each milestone only when reached; extra fix commits are acceptable. Comments in Java must also be short and clear. For documentation changes, run the starter checks and review the rendered document if changed. For code changes, also run debug build, Java unit tests and Android lint, followed by relevant device checks. Resolve failures before calling the feature complete.

## Automation

The project includes a GitHub Actions workflow for pushes and pull requests. It checks documentation, whitespace and the Gradle wrapper, then runs assembleDebug, testDebugUnitTest and lintDebug with JDK 17 pinned in .java-version. It uploads the debug APK after a successful build. The initial screen-only milestone has no unit test classes; a NO-SOURCE unit-test task is not a passed behavioural test. Device checks remain separate.

## Repository and identity

Use a real repository named smart-pantry-manager under the connected account. Keep genuine author attribution. A separate practice repository is possible, but a pseudo profile is not required for this project. No remote repository is claimed until creation and push succeed. Do not start application development until the remote is set up, to meet the brief requirement to use GitHub from the start.

# 7 Evidence and delivery

## Repository contents

Include the Android project, Gradle wrapper, README, docs/FRS.md, development plan and actual test log. Exclude local.properties, secrets, IDE caches and generated build folders. README must state the app purpose, SQLite choice and reason, exact tested tool versions and reliable setup/run commands after implementation. The starter README clearly identifies pending work.

## Report

Prepare the specified cover page and originality declaration, table of contents, introduction, screen flow and data model diagrams, real screenshots of every screen and core function, 3 to 5 explained snippets, 2 to 3 actual challenges, reflection and Harvard references. Use Times New Roman 12 with 1.5 spacing. Include a working GitHub link. This FRS does not replace that report or invent testing evidence.

## Video

Record one narrated video lasting 5 to 7 minutes. Suggested total: 45 seconds for GitHub history, 2 minutes for live CRUD, matching and restart persistence, 2 minutes 30 seconds explaining three code concepts, and 45 seconds for the database choice. This totals 6 minutes. Show your actual code and explain it in your own words; keep the recording legible.

## Submission

Place the full Android project, video file and report inside Studentnumber_Surname_MobileAppDev700_Assignment.zip. The full ZIP must be no larger than 50 MB. Exclude unnecessary build output and compress the video while keeping text readable. The brief allows Word or PDF for the report in sections 6 and 7, but its opening instructions say PDF; confirm the accepted final report format on Moodle before packaging.

## Completion conditions

Finish every acceptance check and capture actual results. Confirm repository access, at least 10 meaningful development commits, reproducible setup, a narrated video within the time limit, complete report content and ZIP size. Student number, due date, GitHub owner, final toolchain and test device are still to be supplied or confirmed.

## Reference

Richfield Graduate Institute of Technology (n.d.) Mobile App Development 700 Practical Assignment: Smart Pantry Manager. Supplied assignment brief, sections 2 to 9, pp. 2 to 8. Filename: Mobile App Development 700 ASSIGNMENT (1).pdf. Accessed 23 October 2026.
