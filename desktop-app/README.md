# The Third Act — Desktop App 🎬

The core JavaFX application for cinematic movie journaling.

## Features
- **Cinematic Dash** — Entry grid with high-res backdrops.
- **AI Import** — Handwritten notes analysis via Gemini 2.0 Flash.
- **Theme Support** — Seamless toggle between Dark (Navy) and Light (Cream).
- **Tracker** — Monthly cinema visit progress.

## Run Locally

### 1. Prerequisites
- JDK 19+
- Maven 3.8+

### 2. Configure
Add your TMDb and Gemini keys to `desktop-app/.env`:
```env
TMDB_API_KEY=your_key
GEMINI_API_KEY=your_key
```

### 3. Launch
```bash
mvn javafx:run
```

---
*For full project documentation, see the [Root README](../README.md).*
