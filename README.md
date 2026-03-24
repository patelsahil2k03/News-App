# NewsFresh - Android News App

**Academic Android Project** | Kotlin + Volley + RecyclerView  
**Purpose:** Fetch and display top headlines from NewsAPI.

---

## Overview

NewsFresh retrieves India top-headline news from NewsAPI and renders results in a RecyclerView. This repository has been hardened for production-readiness by removing hardcoded secrets and improving runtime error handling.

---

## Production upgrades applied

- Removed hardcoded API key from source code.
- Added `BuildConfig.NEWS_API_KEY` usage from local gradle property.
- Added loading and empty/error states in UI.
- Added safer adapter click handling.
- Added image placeholders/fallbacks.
- Removed deprecated Kotlin Android Extensions synthetic view access.

---

## Security setup

Define your API key locally in `local.properties` (do not commit):

```properties
NEWS_API_KEY=your_newsapi_key_here
```

Get a key from: https://newsapi.org/

---

## Runbook

```bash
cd News-App
./gradlew test
./gradlew assembleDebug
```

Open in Android Studio to run on emulator/device.

---

## Notes

- Internet permission is enabled in `AndroidManifest.xml`.
- Uses Custom Tabs to open full article URLs.

---

## License

MIT License (see `LICENSE`).
