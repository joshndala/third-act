package com.thirdact.controller;

import com.thirdact.dao.JournalEntryDAO;
import com.thirdact.model.JournalEntry;
import com.thirdact.model.TmdbMovie;
import com.thirdact.service.GeminiService;
import com.thirdact.view.EntryFormView;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for the journal entry form.
 * Handles save/update and AI import operations on background threads.
 */
public class EntryController {

    private final MainController mainController;
    private final EntryFormView view;
    private final JournalEntryDAO dao;
    private final GeminiService geminiService;

    private JournalEntry existingEntry; // null if creating new

    public EntryController(MainController mainController) {
        this.mainController = mainController;
        this.dao = new JournalEntryDAO();
        this.geminiService = new GeminiService();
        this.view = new EntryFormView(this);
    }

    /**
     * Loads the form with data from a TMDb search result (new entry).
     */
    public void loadFromTmdb(TmdbMovie movie) {
        this.existingEntry = null;

        JournalEntry entry = new JournalEntry()
                .setTmdbId(movie.getId())
                .setTitle(movie.getTitle())
                .setReleaseYear(movie.getReleaseYear())
                .setPosterUrl(movie.getPosterUrlLarge()) // w500 — crisp at 120px display
                .setBackdropUrl(movie.getBackdropUrl()) // w1280 — cinematic quality
                .setWatchDate(LocalDate.now().toString());

        view.populateForm(entry, false);
    }

    /**
     * Loads the form with an existing entry for editing.
     */
    public void loadExistingEntry(JournalEntry entry) {
        this.existingEntry = entry;
        view.populateForm(entry, true);
    }

    /**
     * Saves the entry (insert or update) on a background thread.
     */
    public void saveEntry(JournalEntry entry) {
        view.setSaving(true);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (existingEntry != null) {
                    entry.setId(existingEntry.getId());
                    dao.updateEntry(entry);
                } else {
                    dao.insertEntry(entry);
                }
                return null;
            }
        };

        saveTask.setOnSucceeded(event -> Platform.runLater(() -> {
            view.setSaving(false);
            mainController.showDashboard();
        }));

        saveTask.setOnFailed(event -> {
            Throwable error = saveTask.getException();
            Platform.runLater(() -> {
                view.setSaving(false);
                view.showError("Save failed: " + error.getMessage());
            });
            error.printStackTrace();
        });

        Thread thread = new Thread(saveTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Sends the uploaded files to Gemini for word-for-word text extraction,
     * then populates the form fields with the AI result.
     *
     * @param files List of image (jpg/png/webp) or PDF files selected by the user.
     */
    public void importFromFiles(List<File> files) {
        if (files == null || files.isEmpty())
            return;

        // Check for Gemini API key before importing
        if (com.thirdact.service.ConfigManager.getInstance().getGeminiApiKey().isBlank()) {
            mainController.showSettingsWithWarning(
                    "Please enter your Gemini API key to enable AI transcription.",
                    false, true);
            return;
        }

        view.setImporting(true);

        Task<Map<String, String>> importTask = new Task<>() {
            @Override
            protected Map<String, String> call() throws Exception {
                return geminiService.analyzeNotes(files);
            }
        };

        importTask.setOnSucceeded(event -> Platform.runLater(() -> {
            view.setImporting(false);
            view.fillFromAI(importTask.getValue());
        }));

        importTask.setOnFailed(event -> {
            Throwable error = importTask.getException();
            Platform.runLater(() -> {
                view.setImporting(false);
                view.showError("Import failed: " + error.getMessage());
            });
            System.err.println("[EntryController] AI import failed: " + error.getMessage());
        });

        Thread thread = new Thread(importTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Deletes the current entry on a background thread.
     */
    public void deleteEntry() {
        if (existingEntry == null)
            return;

        view.setSaving(true);

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                dao.deleteEntry(existingEntry.getId());
                return null;
            }
        };

        deleteTask.setOnSucceeded(event -> Platform.runLater(() -> {
            view.setSaving(false);
            mainController.showDashboard();
        }));

        deleteTask.setOnFailed(event -> {
            Throwable error = deleteTask.getException();
            Platform.runLater(() -> {
                view.setSaving(false);
                view.showError("Delete failed: " + error.getMessage());
            });
            error.printStackTrace();
        });

        Thread thread = new Thread(deleteTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Cancels the form and returns to the dashboard.
     */
    public void cancel() {
        mainController.showDashboard();
    }

    public EntryFormView getView() {
        return view;
    }
}
