package com.thirdact.controller;

import com.thirdact.dao.DatabaseManager;
import com.thirdact.model.JournalEntry;
import com.thirdact.model.TmdbMovie;
import com.thirdact.view.DashboardView;
import com.thirdact.view.EntryFormView;
import com.thirdact.view.SearchView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main controller that owns the Stage and manages view navigation.
 * Acts as the central coordinator in the MVC pattern.
 */
public class MainController {

    private final Stage stage;
    private final StackPane rootPane;
    private final Scene scene;

    private DashboardView dashboardView;
    private SearchView searchView;
    private EntryFormView entryFormView;

    private SearchController searchController;
    private EntryController entryController;

    public MainController(Stage stage) {
        this.stage = stage;
        this.rootPane = new StackPane();
        rootPane.getStyleClass().add("root-pane");

        this.scene = new Scene(rootPane, 1100, 750);

        // Load stylesheet
        String css = getClass().getClassLoader().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Initialize database
        try {
            DatabaseManager.getInstance().initialize();
        } catch (Exception e) {
            System.err.println("[MainController] Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Initialize views and sub-controllers
        initializeViews();

        // Configure stage
        stage.setScene(scene);
        stage.setTitle("The Third Act");
        stage.setMinWidth(900);
        stage.setMinHeight(650);

        // Clean up on close
        stage.setOnCloseRequest(event -> DatabaseManager.getInstance().close());

        // Show dashboard
        showDashboard();
    }

    private void initializeViews() {
        dashboardView = new DashboardView(this);
        searchController = new SearchController(this);
        entryController = new EntryController(this);

        searchView = searchController.getView();
        entryFormView = entryController.getView();
    }

    /**
     * Navigates to the Dashboard view.
     */
    public void showDashboard() {
        dashboardView.refresh();
        rootPane.getChildren().setAll(dashboardView.getRoot());
    }

    /**
     * Navigates to the Search view.
     */
    public void showSearch() {
        searchController.reset();
        rootPane.getChildren().setAll(searchView.getRoot());
    }

    /**
     * Navigates to the Entry Form for a new entry, pre-filled from a TMDb movie.
     */
    public void showEntryForm(TmdbMovie movie) {
        entryController.loadFromTmdb(movie);
        rootPane.getChildren().setAll(entryFormView.getRoot());
    }

    /**
     * Navigates to the Entry Form for editing an existing entry.
     */
    public void showEntryFormForEdit(JournalEntry entry) {
        entryController.loadExistingEntry(entry);
        rootPane.getChildren().setAll(entryFormView.getRoot());
    }

    public Stage getStage() {
        return stage;
    }
}
