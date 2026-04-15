# MPlayer

An Android music preview player built with modern Android development practices, powered by the iTunes Search API.

## Screenshots

<!-- Add screenshots here -->

## Features

- **Search** — query songs via the iTunes Search API with offset-based pagination
- **Player** — play 30-second song previews with playback controls and a progress timeline
- **Queue** — recently played history (up to 10 songs, persisted across sessions)
- **Album view** — browse an album's full track list and play any track
- **Adaptive layout** — responsive UI for phones, tablets, and foldable devices

## Architecture

The project follows **Clean Architecture** with an **MVVM** presentation layer, organized into four main layers:

```
features/       ← Compose screens + HiltViewModels (UI state via StateFlow)
data/           ← Repository implementations, DTOs, API mappers, local cache
domain/         ← Pure Kotlin models (Song, Album), no Android dependencies
core/           ← DI modules, navigation, networking, theme, adaptive utilities
```

Key design decisions:
- ViewModels expose a single `UiState` via `StateFlow`; screens are stateless composables
- Domain models are never bypassed — DTOs stay in the data layer
- `PlaybackController` interface wraps ExoPlayer, keeping media logic out of the UI
- `WindowSizeClass` drives adaptive layouts; list-detail scaffold on expanded widths

## Tech Stack

| Category | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Architecture | Hilt, ViewModel, Navigation Compose |
| Networking | Retrofit + OkHttp + kotlinx.serialization |
| Pagination | Paging 3 |
| Media | Media3 ExoPlayer |
| Adaptive UI | material3-adaptive, WindowSizeClass |
| Image loading | Coil |
| Testing | JUnit 4, MockK, Turbine, MockWebServer |

## Requirements

- Android 10+ (minSdk 29)
- No API key required (iTunes Search API is public)

## Building

```bash
./gradlew assembleDebug
```
