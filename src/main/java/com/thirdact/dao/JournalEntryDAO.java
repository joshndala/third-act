package com.thirdact.dao;

import com.thirdact.model.JournalEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for JournalEntry CRUD operations.
 * All methods use PreparedStatements to prevent SQL injection.
 */
public class JournalEntryDAO {

    private final Connection connection;

    public JournalEntryDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Inserts a new journal entry and returns the generated ID.
     */
    public int insertEntry(JournalEntry entry) throws SQLException {
        String sql = """
                INSERT INTO journal_entries
                (tmdb_id, title, release_year, poster_url, backdrop_url,
                 rating, summary, vibe, peak_moment, extra_notes,
                 watched_in_theaters, watch_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindEntryParameters(pstmt, entry);
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    entry.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves all journal entries, most recent first.
     */
    public List<JournalEntry> getAllEntries() throws SQLException {
        String sql = "SELECT * FROM journal_entries ORDER BY watch_date DESC, id DESC";
        List<JournalEntry> entries = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }
        return entries;
    }

    /**
     * Retrieves entries for a specific month (for the cinema tracker).
     *
     * @param year  e.g. 2026
     * @param month 1–12
     */
    public List<JournalEntry> getEntriesByMonth(int year, int month) throws SQLException {
        String monthStr = String.format("%04d-%02d", year, month);
        String sql = "SELECT * FROM journal_entries WHERE watch_date LIKE ? || '%' ORDER BY watch_date DESC";

        List<JournalEntry> entries = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, monthStr);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
            }
        }
        return entries;
    }

    /**
     * Returns the count of theater-watched movies for a given month.
     */
    public int getTheaterCountForMonth(int year, int month) throws SQLException {
        String monthStr = String.format("%04d-%02d", year, month);
        String sql = """
                SELECT COUNT(*) FROM journal_entries
                WHERE watch_date LIKE ? || '%' AND watched_in_theaters = 1
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, monthStr);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Updates an existing journal entry.
     */
    public boolean updateEntry(JournalEntry entry) throws SQLException {
        String sql = """
                UPDATE journal_entries SET
                    tmdb_id = ?, title = ?, release_year = ?, poster_url = ?,
                    backdrop_url = ?, rating = ?, summary = ?, vibe = ?,
                    peak_moment = ?, extra_notes = ?, watched_in_theaters = ?,
                    watch_date = ?
                WHERE id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            bindEntryParameters(pstmt, entry);
            pstmt.setInt(13, entry.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a journal entry by its ID.
     */
    public boolean deleteEntry(int id) throws SQLException {
        String sql = "DELETE FROM journal_entries WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // --- Private Helpers ---

    private void bindEntryParameters(PreparedStatement pstmt, JournalEntry e) throws SQLException {
        pstmt.setInt(1, e.getTmdbId());
        pstmt.setString(2, e.getTitle());
        pstmt.setInt(3, e.getReleaseYear());
        pstmt.setString(4, e.getPosterUrl());
        pstmt.setString(5, e.getBackdropUrl());
        pstmt.setDouble(6, e.getRating());
        pstmt.setString(7, e.getSummary());
        pstmt.setString(8, e.getVibe());
        pstmt.setString(9, e.getPeakMoment());
        pstmt.setString(10, e.getExtraNotes());
        pstmt.setInt(11, e.isWatchedInTheaters() ? 1 : 0);
        pstmt.setString(12, e.getWatchDate());
    }

    private JournalEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        return new JournalEntry()
                .setId(rs.getInt("id"))
                .setTmdbId(rs.getInt("tmdb_id"))
                .setTitle(rs.getString("title"))
                .setReleaseYear(rs.getInt("release_year"))
                .setPosterUrl(rs.getString("poster_url"))
                .setBackdropUrl(rs.getString("backdrop_url"))
                .setRating(rs.getDouble("rating"))
                .setSummary(rs.getString("summary"))
                .setVibe(rs.getString("vibe"))
                .setPeakMoment(rs.getString("peak_moment"))
                .setExtraNotes(rs.getString("extra_notes"))
                .setWatchedInTheaters(rs.getInt("watched_in_theaters") == 1)
                .setWatchDate(rs.getString("watch_date"));
    }
}
