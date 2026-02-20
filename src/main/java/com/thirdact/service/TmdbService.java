package com.thirdact.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thirdact.model.TmdbMovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Service layer for querying The Movie Database (TMDb) API.
 * Uses java.net.http.HttpClient (built-in JDK).
 *
 * API key resolution order:
 * 1. TMDB_API_KEY environment variable
 * 2. .env file in the working directory
 * 3. config.properties on the classpath (fallback)
 */
public class TmdbService {

    private static final String BASE_URL = "https://api.themoviedb.org/3";

    private final String apiKey;
    private final HttpClient httpClient;

    public TmdbService() {
        this.apiKey = resolveApiKey();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        if (apiKey.isBlank() || apiKey.equals("YOUR_TMDB_API_KEY_HERE")) {
            System.err.println("[TmdbService] WARNING: No valid TMDb API key found!");
            System.err.println("  → Set TMDB_API_KEY in your .env file or as an environment variable.");
        }
    }

    /**
     * Searches TMDb for movies matching the query string.
     */
    public List<TmdbMovie> searchMovies(String query) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = BASE_URL + "/search/movie?api_key=" + apiKey
                + "&query=" + encoded
                + "&include_adult=false&language=en-US&page=1";

        String json = sendGet(url);
        return parseSearchResults(json);
    }

    /**
     * Fetches detailed movie information by TMDb ID.
     */
    public TmdbMovie getMovieDetails(int tmdbId) throws IOException, InterruptedException {
        String url = BASE_URL + "/movie/" + tmdbId + "?api_key=" + apiKey + "&language=en-US";
        String json = sendGet(url);
        return parseSingleMovie(JsonParser.parseString(json).getAsJsonObject());
    }

    // --- Private Helpers ---

    private String sendGet(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("TMDb API returned status " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    private List<TmdbMovie> parseSearchResults(String json) {
        List<TmdbMovie> movies = new ArrayList<>();
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray results = root.getAsJsonArray("results");

        if (results != null) {
            for (JsonElement element : results) {
                JsonObject obj = element.getAsJsonObject();
                movies.add(parseSingleMovie(obj));
            }
        }

        return movies;
    }

    private TmdbMovie parseSingleMovie(JsonObject obj) {
        TmdbMovie movie = new TmdbMovie();
        movie.setId(getIntSafe(obj, "id"));
        movie.setTitle(getStringSafe(obj, "title"));
        movie.setReleaseDate(getStringSafe(obj, "release_date"));
        movie.setOverview(getStringSafe(obj, "overview"));
        movie.setPosterPath(getStringSafe(obj, "poster_path"));
        movie.setBackdropPath(getStringSafe(obj, "backdrop_path"));
        return movie;
    }

    private String getStringSafe(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        return (el != null && !el.isJsonNull()) ? el.getAsString() : null;
    }

    private int getIntSafe(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        return (el != null && !el.isJsonNull()) ? el.getAsInt() : 0;
    }

    /**
     * Resolves the TMDb API key in order:
     * 1. TMDB_API_KEY environment variable (CI / shell export)
     * 2. .env file in the working directory (local dev)
     * 3. ~/.thirdact/config.properties (packaged DMG — user's own key)
     * 4. config.properties on the classpath (last-resort fallback)
     */
    private String resolveApiKey() {
        // 1. Environment variable
        String envKey = System.getenv("TMDB_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            System.out.println("[TmdbService] API key loaded from environment variable.");
            return envKey.trim();
        }

        // 2. .env file (development)
        Path envFile = Path.of(".env");
        if (Files.exists(envFile)) {
            try (BufferedReader reader = Files.newBufferedReader(envFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("TMDB_API_KEY=")) {
                        String value = line.substring("TMDB_API_KEY=".length()).trim();
                        if (!value.isBlank()) {
                            System.out.println("[TmdbService] API key loaded from .env file.");
                            return value;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("[TmdbService] Error reading .env file: " + e.getMessage());
            }
        }

        // 3. ~/.thirdact/config.properties (packaged app — saved by first-launch setup)
        Path userConfig = Path.of(System.getProperty("user.home"), ".thirdact", "config.properties");
        if (Files.exists(userConfig)) {
            try (InputStream is = Files.newInputStream(userConfig)) {
                Properties props = new Properties();
                props.load(is);
                String key = props.getProperty("tmdb.api_key", "").trim();
                if (!key.isBlank()) {
                    System.out.println("[TmdbService] API key loaded from ~/.thirdact/config.properties.");
                    return key;
                }
            } catch (IOException e) {
                System.err.println("[TmdbService] Error reading user config: " + e.getMessage());
            }
        }

        // 4. Classpath config.properties (last resort)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String propKey = props.getProperty("tmdb.api_key", "");
                if (!propKey.isBlank()) {
                    System.out.println("[TmdbService] API key loaded from classpath config.properties.");
                    return propKey;
                }
            }
        } catch (IOException e) {
            System.err.println("[TmdbService] Error reading classpath config: " + e.getMessage());
        }

        return "";
    }
}
