package com.thirdact.model;

/**
 * Lightweight DTO for TMDb movie search results.
 */
public class TmdbMovie {

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    private int id;
    private String title;
    private String releaseDate; // "YYYY-MM-DD" from TMDb
    private String overview;
    private String posterPath; // e.g. "/abc123.jpg"
    private String backdropPath; // e.g. "/xyz789.jpg"

    public TmdbMovie() {
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    // --- Setters ---

    public TmdbMovie setId(int id) {
        this.id = id;
        return this;
    }

    public TmdbMovie setTitle(String title) {
        this.title = title;
        return this;
    }

    public TmdbMovie setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public TmdbMovie setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public TmdbMovie setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public TmdbMovie setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    // --- URL Builders ---

    /**
     * Full poster URL at w342 size (good for list thumbnails).
     */
    public String getPosterUrl() {
        return posterPath != null ? IMAGE_BASE_URL + "w342" + posterPath : null;
    }

    /**
     * Full poster URL at w500 size (good for detail views).
     */
    public String getPosterUrlLarge() {
        return posterPath != null ? IMAGE_BASE_URL + "w500" + posterPath : null;
    }

    /**
     * Full backdrop URL at w1280 size (cinematic quality).
     */
    public String getBackdropUrl() {
        return backdropPath != null ? IMAGE_BASE_URL + "w1280" + backdropPath : null;
    }

    /**
     * Full backdrop URL at original resolution.
     */
    public String getBackdropUrlOriginal() {
        return backdropPath != null ? IMAGE_BASE_URL + "original" + backdropPath : null;
    }

    /**
     * Extracts the release year from the release date string.
     */
    public int getReleaseYear() {
        if (releaseDate != null && releaseDate.length() >= 4) {
            try {
                return Integer.parseInt(releaseDate.substring(0, 4));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return title + " (" + getReleaseYear() + ")";
    }
}
