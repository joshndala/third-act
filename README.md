# The Third Act 🎬

A visually rich, cinematic movie journaling experience. Capture how films make you feel, tracked beautifully.

This project consists of two main components:
1.  **[Desktop App](file:///Users/joshua_ndala/repos/third-act/desktop-app)** — The core JavaFX application for journaling and tracking.
2.  **[Landing Page](file:///Users/joshua_ndala/repos/third-act/landing-page)** — A modern Vue.js marketing site.

---

## 💻 Desktop Application

A local-first, privacy-focused movie journal with AI-powered features.

### Features
- **Cinematic Dashboard** — High-res backdrop cards with gradient overlays.
- **AI-Powered Archiving** — Import handwritten notes via Gemini 2.0 Flash (OCR & summarization).
- **Movie Search** — Powered by TMDb for metadata and high-res assets.
- **Customizable Appearance** — Switch between brand Navy (Dark) and Cream (Light) themes.
- **Local & Private** — All data stays on your machine in a SQLite database.

### Tech Stack
- **Java 19** & **JavaFX 19** (pure code, no FXML)
- **SQLite** for local storage
- **Gemini API** (Google AI) for note analysis
- **TMDb API** for movie intelligence
- **Maven** for dependencies

---

## 🎨 Brand Palette
- **Navy** `#2B3358` (Primary/Dark Mode)
- **Gold** `#D4A15C` (Accent)
- **Cream** `#F5F2E9` (Background/Light Mode)

---

## 🚀 Getting Started

### Prerequisites
- **JDK 19+**
- **Maven 3.8+**
- **Node.js 18+**

### 1. Setup API Keys
Create a `.env` file in `desktop-app/`:
```env
TMDB_API_KEY=your_tmdb_key
GEMINI_API_KEY=your_gemini_key
```

### 2. Run the Desktop App
```bash
cd desktop-app
mvn javafx:run
```

### 3. Run the Landing Page
```bash
cd landing-page
npm install
npm run dev
```

---

## 📂 Project Structure
```
third-act/
├── desktop-app/      # JavaFX application source
├── landing-page/     # Vue.js marketing site
└── assets/           # Shared brand assets & icons
```

---

## License
MIT
