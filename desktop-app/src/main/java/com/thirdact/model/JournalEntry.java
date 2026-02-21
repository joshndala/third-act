package com.thirdact.model;

/**
 * Represents a single movie journal entry.
 * Maps directly to the journal_entries SQLite table.
 */
public class JournalEntry {

    private int id;
    private int tmdbId;
    private String title;
    private int releaseYear;
    private String posterUrl;
    private String backdropUrl;
    private double rating;
    private String summary;
    private String vibe;
    private String peakMoment;
    private String extraNotes;
    private boolean watchedInTheaters;
    private String watchDate; // YYYY-MM-DD

    public JournalEntry() {
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public double getRating() {
        return rating;
    }

    public String getSummary() {
        return summary;
    }

    public String getVibe() {
        return vibe;
    }

    public String getPeakMoment() {
        return peakMoment;
    }

    public String getExtraNotes() {
        return extraNotes;
    }

    public boolean isWatchedInTheaters() {
        return watchedInTheaters;
    }

    public String getWatchDate() {
        return watchDate;
    }

    // --- Fluent Setters ---

    public JournalEntry setId(int id) {
        this.id = id;
        return this;
    }

    public JournalEntry setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
        return this;
    }

    public JournalEntry setTitle(String title) {
        this.title = title;
        return this;
    }

    public JournalEntry setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public JournalEntry setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
        return this;
    }

    public JournalEntry setBackdropUrl(String backdropUrl) {
        this.backdropUrl = backdropUrl;
        return this;
    }

    public JournalEntry setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public JournalEntry setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public JournalEntry setVibe(String vibe) {
        this.vibe = vibe;
        return this;
    }

    public JournalEntry setPeakMoment(String peakMoment) {
        this.peakMoment = peakMoment;
        return this;
    }

    public JournalEntry setExtraNotes(String extraNotes) {
        this.extraNotes = extraNotes;
        return this;
    }

    public JournalEntry setWatchedInTheaters(boolean watchedInTheaters) {
        this.watchedInTheaters = watchedInTheaters;
        return this;
    }

    public JournalEntry setWatchDate(String watchDate) {
        this.watchDate = watchDate;
        return this;
    }

    @Override
    public String toString() {
        return title + " (" + releaseYear + ") — " + rating + "★";
    }
}
