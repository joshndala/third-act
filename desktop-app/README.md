# The Third Act 🎬

A visually rich, local-first movie journaling application built with JavaFX. More than a tracker — a cinematic experience for capturing how films make you feel.

---

## Features

- **Movie Search** — Query TMDb for any film and pull in metadata, posters, and high-res backdrops
- **Journal Entries** — Write four distinct reflections per film: *The Summary*, *The Vibe*, *The Peak Moment*, and *Extra Notes*
- **Half-Star Ratings** — Letterboxd-style 0–5 rating with 0.5 precision
- **Cinematic Dashboard** — Entries displayed as backdrop image cards with a gradient overlay
- **Cinema Tracker** — Tracks progress toward a goal of 2 theater watches per month
- **Local & Private** — All data stored in a single SQLite file on your machine (`~/.thirdact/thirdact.db`)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 19 |
| UI | JavaFX 19 (pure code, no FXML) |
| Database | SQLite via `sqlite-jdbc` |
| API | [TMDb](https://www.themoviedb.org/) |
| JSON | Gson |
| Build | Maven |

---

## Getting Started

### Prerequisites

- JDK 19+
- Maven 3.8+

### 1. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/third-act.git
cd third-act
```

### 2. Add your TMDb API key

Create a `.env` file in the project root (already gitignored):

```bash
cp .env.example .env   # or just create it manually
```

Then edit `.env`:

```
TMDB_API_KEY=your_api_key_here
```

Get a free API key at [themoviedb.org/settings/api](https://www.themoviedb.org/settings/api).

### 3. Run the app

```bash
mvn javafx:run
```

The SQLite database is created automatically at `~/.thirdact/thirdact.db` on first launch.

---

## Project Structure

```
src/main/java/com/thirdact/
├── Main.java                    # JavaFX Application entry point
├── model/
│   ├── JournalEntry.java        # Journal entry data model
│   └── TmdbMovie.java           # TMDb search result DTO
├── dao/
│   ├── DatabaseManager.java     # SQLite connection singleton
│   └── JournalEntryDAO.java     # CRUD operations
├── service/
│   └── TmdbService.java         # TMDb API client
├── controller/
│   ├── MainController.java      # Navigation & stage management
│   ├── SearchController.java    # Movie search logic
│   └── EntryController.java     # Entry save/update logic
└── view/
    ├── DashboardView.java        # Main backdrop card grid
    ├── SearchView.java           # TMDb search interface
    ├── EntryFormView.java        # Journal entry form
    └── StarRatingControl.java   # Custom half-star rating widget
```

---

## Environment Variables

| Variable | Description |
|---|---|
| `TMDB_API_KEY` | Your TMDb v3 API key |

The app resolves the key in this order: **environment variable → `.env` file → `config.properties`** (classpath fallback).

---

## License

MIT
