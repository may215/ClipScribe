# ClipScribe (Android)

Create short clips from shared YouTube links (timestamps), edit range, and view a synchronized transcript.
RTL Hebrew-first UI and dark theme.

⚠️ This app does NOT download YouTube audio/video. Transcript providers must use legal/authorized sources.

## Features
- Share YouTube link → opens clip screen automatically
- Default clip length toggle: 30s / 1m / 2m / 5m
- Range editor (Start/End)
- Embedded YouTube player (starts at Start, pauses at End)
- Transcript with timestamps, highlight, auto-scroll, seek-on-tap
- Save to Library (Room)
- Export transcript: Markdown + PDF
- Copy / Share transcript

## Build
Requirements: Android Studio, JDK 17

```bash
./gradlew assembleDebug
```

## Notes
- Transcript in this repo uses a demo provider (placeholder).
