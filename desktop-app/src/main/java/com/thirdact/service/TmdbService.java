package com.thirdact.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thirdact.model.TmdbMovie;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for querying The Movie Database (TMDb) API.
 * Uses java.net.http.HttpClient (built-in JDK).
 *
 * API key resolution is delegated to ConfigManager.
 */
public class TmdbService {

    private static final String BASE_URL = "https://api.themoviedb.org/3";

    private final HttpClient httpClient;

    public TmdbService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Searches TMDb for movies matching the query string.
     */
    public List<TmdbMovie> searchMovies(String query) throws IOException, InterruptedException {
        String apiKey = ConfigManager.getInstance().getTmdbApiKey();
        if (apiKey.isBlank() || apiKey.equals("YOUR_TMDB_API_KEY_HERE")) {
            throw new IllegalStateException("TMDb API key is missing or invalid.");
        }

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
        String apiKey = ConfigManager.getInstance().getTmdbApiKey();
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
}
