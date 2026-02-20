package com.thirdact.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton manager for the local SQLite database connection.
 * Stores the database file at ~/.thirdact/thirdact.db
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private static final String DB_DIRECTORY = System.getProperty("user.home") + File.separator + ".thirdact";
    private static final String DB_PATH = DB_DIRECTORY + File.separator + "thirdact.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    private DatabaseManager() {
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initializes the database: ensures the directory exists,
     * opens the connection, and creates the schema.
     */
    public void initialize() throws SQLException {
        // Ensure the database directory exists
        File dbDir = new File(DB_DIRECTORY);
        if (!dbDir.exists()) {
            boolean created = dbDir.mkdirs();
            if (!created) {
                throw new SQLException("Failed to create database directory: " + DB_DIRECTORY);
            }
        }

        // Open connection
        connection = DriverManager.getConnection(DB_URL);
        System.out.println("[DatabaseManager] Connected to SQLite at: " + DB_PATH);

        // Enable WAL mode for better concurrent read performance
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
        }

        // Create schema
        createTables();
    }

    /**
     * Creates the journal_entries table if it does not exist.
     */
    private void createTables() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS journal_entries (
                    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
                    tmdb_id            INTEGER,
                    title              TEXT NOT NULL,
                    release_year       INTEGER,
                    poster_url         TEXT,
                    backdrop_url       TEXT,
                    rating             REAL,
                    summary            TEXT,
                    vibe               TEXT,
                    peak_moment        TEXT,
                    extra_notes        TEXT,
                    watched_in_theaters INTEGER DEFAULT 0,
                    watch_date         TEXT
                );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DatabaseManager] Schema initialized.");
        }
    }

    /**
     * Returns the active database connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the database connection gracefully.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DatabaseManager] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DatabaseManager] Error closing connection: " + e.getMessage());
            }
        }
    }
}
