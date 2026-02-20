package com.thirdact.controller;

import com.thirdact.model.TmdbMovie;
import com.thirdact.service.TmdbService;
import com.thirdact.view.SearchView;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;

/**
 * Controller for the movie search functionality.
 * Executes TMDb API calls on background threads.
 */
public class SearchController {

    private final MainController mainController;
    private final SearchView view;
    private final TmdbService tmdbService;

    public SearchController(MainController mainController) {
        this.mainController = mainController;
        this.tmdbService = new TmdbService();
        this.view = new SearchView(this);
    }

    /**
     * Initiates a movie search on a background thread.
     */
    public void performSearch(String query) {
        if (query == null || query.isBlank())
            return;

        view.showLoading(true);
        view.clearResults();

        Task<List<TmdbMovie>> searchTask = new Task<>() {
            @Override
            protected List<TmdbMovie> call() throws Exception {
                return tmdbService.searchMovies(query);
            }
        };

        searchTask.setOnSucceeded(event -> {
            List<TmdbMovie> results = searchTask.getValue();
            Platform.runLater(() -> {
                view.showLoading(false);
                view.displayResults(results);
            });
        });

        searchTask.setOnFailed(event -> {
            Throwable error = searchTask.getException();
            Platform.runLater(() -> {
                view.showLoading(false);
                view.showError("Search failed: " + error.getMessage());
            });
            error.printStackTrace();
        });

        Thread thread = new Thread(searchTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Called when a user selects a movie from the search results.
     */
    public void onMovieSelected(TmdbMovie movie) {
        mainController.showEntryForm(movie);
    }

    /**
     * Navigates back to the dashboard.
     */
    public void goBack() {
        mainController.showDashboard();
    }

    /**
     * Resets the search view for a fresh search.
     */
    public void reset() {
        view.clearResults();
        view.clearSearchField();
    }

    public SearchView getView() {
        return view;
    }
}
