# Agent Instructions (Task Split)

## Task 0 — Build Verification
- Unzip repository
- Run: ./gradlew assembleDebug
- Fix any Gradle/Android toolchain issues if needed

## Task 1 — UI Polish
- Ensure RTL is consistent across all screens
- Improve typography spacing for Hebrew
- Replace placeholder title with fetched YouTube metadata (optional, safe)

## Task 2 — Transcript Provider (Safe)
- Keep DemoTranscriptProvider as default
- Add interface implementation for legal captions source if available (do NOT add media downloading)
- Ensure timestamps + highlight + auto-scroll remain intact

## Task 3 — Library + Persistence
- Confirm Room migrations and schema stability
- Add tests for DAO and Repository if feasible

## Task 4 — Export
- Validate Markdown export produces correct file
- Validate PDF export works on-device (A4 single page MVP)
- Consider multi-page PDF if needed

## Definition of Done
- App builds: ./gradlew assembleDebug
- Share YouTube link opens ClipScreen with correct timestamp
- Save clip → appears in Library
- Export MD/PDF works via Storage Access Framework
