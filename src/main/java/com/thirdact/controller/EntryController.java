package com.thirdact.controller;

import com.thirdact.dao.JournalEntryDAO;
import com.thirdact.model.JournalEntry;
import com.thirdact.model.TmdbMovie;
import com.thirdact.view.EntryFormView;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.time.LocalDate;

/**
 * Controller for the journal entry form.
 * Handles save/update operations on background threads.
 */
public class EntryController {

    private final MainController mainController;
    private final EntryFormView view;
    private final JournalEntryDAO dao;

    private JournalEntry existingEntry; // null if creating new

    public EntryController(MainController mainController) {
        this.mainController = mainController;
        this.dao = new JournalEntryDAO();
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
     * Cancels the form and returns to the dashboard.
     */
    public void cancel() {
        mainController.showDashboard();
    }

    public EntryFormView getView() {
        return view;
    }
}
