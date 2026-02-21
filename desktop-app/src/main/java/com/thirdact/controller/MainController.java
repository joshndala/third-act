package com.thirdact.controller;

import com.thirdact.dao.DatabaseManager;
import com.thirdact.model.JournalEntry;
import com.thirdact.model.TmdbMovie;
import com.thirdact.service.ConfigManager;
import com.thirdact.view.DashboardView;
import com.thirdact.view.EntryFormView;
import com.thirdact.view.SearchView;
import com.thirdact.view.SettingsView;
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

    private String lightCssUrl;

    private DashboardView dashboardView;
    private SearchView searchView;
    private EntryFormView entryFormView;
    private SettingsView settingsView;

    private SearchController searchController;
    private EntryController entryController;

    public MainController(Stage stage) {
        this.stage = stage;
        this.rootPane = new StackPane();
        rootPane.getStyleClass().add("root-pane");

        this.scene = new Scene(rootPane, 1100, 750);

        // Load base (dark) stylesheet
        String css = getClass().getClassLoader().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Cache the light-mode override URL
        lightCssUrl = getClass().getClassLoader().getResource("theme-light.css").toExternalForm();

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

        // Apply saved/system theme before showing the window
        applyTheme(ConfigManager.getInstance().getTheme());

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
     * Applies the given theme. Call any time to switch instantly.
     *
     * @param mode "dark", "light", or "system"
     */
    public void applyTheme(String mode) {
        boolean useLightMode = switch (mode) {
            case "light" -> true;
            case "dark" -> false;
            default -> isSystemDarkMode() ? false : true; // "system"
        };

        if (useLightMode) {
            if (!scene.getStylesheets().contains(lightCssUrl)) {
                scene.getStylesheets().add(lightCssUrl);
            }
        } else {
            scene.getStylesheets().remove(lightCssUrl);
        }
    }

    /**
     * Detects the macOS system appearance via the 'defaults' command.
     * Returns true if the system is currently in Dark Mode.
     */
    private boolean isSystemDarkMode() {
        try {
            Process p = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle")
                    .redirectErrorStream(true)
                    .start();
            String output = new String(p.getInputStream().readAllBytes()).trim();
            p.waitFor();
            return "Dark".equalsIgnoreCase(output);
        } catch (Exception e) {
            // If the command fails (e.g. non-macOS), assume dark
            return true;
        }
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

    /**
     * Navigates to the Settings view.
     * Re-creates each time so fields reflect the latest saved values.
     */
    public void showSettings() {
        settingsView = new SettingsView(this::showDashboard, this);
        rootPane.getChildren().setAll(settingsView.getRoot());
    }

    public Stage getStage() {
        return stage;
    }
}
