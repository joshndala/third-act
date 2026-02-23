# The Third Act — Desktop App 🎬

The core JavaFX application for cinematic movie journaling.

## Features
- **Cinematic Dash** — Entry grid with high-res backdrops.
- **AI Import** — Handwritten notes analysis via Gemini 2.0 Flash. Easily upload photos directly from your phone over the local network via QR Code.
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

## Build & Package (macOS)

To generate a standalone `.dmg` installer:

1. **Build the JAR**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Run jpackage**:
   ```bash
   rm -rf target/jpackage-input target/dist && mkdir -p target/jpackage-input target/dist
   cp target/third-act-1.1.0.jar target/jpackage-input/
   jpackage \
     --input target/jpackage-input \
     --name "The Third Act" \
     --main-jar third-act-1.1.0.jar \
     --main-class com.thirdact.Launcher \
     --type dmg \
     --icon src/main/resources/icon.icns \
     --dest target/dist \
     --app-version 1.1.0 \
     --java-options "--enable-native-access=ALL-UNNAMED"
   ```

Final output will be in `target/dist/`.

---
*For full project documentation, see the [Root README](../README.md).*
