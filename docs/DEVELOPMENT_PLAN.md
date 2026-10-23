# Development plan

Before every feature, read FRS.md and identify the requirement and acceptance check. Keep implementation small and explainable. A milestone can take several real sessions; dates are not prescribed.

| Order | Commit message | Gate before commit |
| --- | --- | --- |
| 1 | Add FRS and development workflow | Review FRS, render document, run starter gate |
| 2 | Create Java Android project | Confirm remote, record versions, build blank app |
| 3 | Add SQLite tables and recipe seeds | Check 20 recipes and no reseeding |
| 4 | Add pantry list and adapter | Check empty state and database rows |
| 5 | Add ingredient form and validation | Check valid and invalid inputs |
| 6 | Add ingredient editing and deletion | Check full CRUD and restart persistence |
| 7 | Add name and unit helpers | Test aliases, conversions and incompatible units |
| 8 | Add strict recipe matching and tests | Test shortages, equality and duplicate totals |
| 9 | Add suggested recipes screen | Show recipe appear and disappear with stock |
| 10 | Add recipe details screen | Check all ingredients and method |
| 11 | Add settings and screen navigation | Check preference, Intents, Back and rotation |
| 12 | Fix issues found during app testing | Resolve actual failures; skip if no fixes needed |
| 13 | Add setup guide and evidence checklist | Reproduce setup and review real evidence |

Milestones 1 through 11 are implemented and checked on the build machine. The user's final emulator acceptance, any resulting fixes, report screenshots and narrated video remain outstanding. Name cleaning was added with milestone 5 and unit conversion helpers were committed separately before matching. Consult TEST_LOG.md for actual results. Never commit planned features before building them. Add meaningful fix commits as necessary.

## Per change pipeline

1. Read the FRS and select a requirement.
2. Implement the smallest complete change.
3. Run bash scripts/check.sh. Once available, it builds, tests and lints Android too.
4. Perform relevant acceptance checks on an emulator or device.
5. Record actual results in TEST_LOG.md and inspect the diff for mistakes or secrets.
6. Commit with a short message, then push to the connected remote.

GitHub Actions repeats the automated checks on push and pull request. A green documentation check does not prove the app works. Device tests and report evidence remain separate.
