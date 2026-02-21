package com.thirdact.view;

import com.thirdact.controller.SearchController;
import com.thirdact.model.TmdbMovie;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Search view for querying TMDb and selecting a movie.
 */
public class SearchView {

    private final SearchController controller;
    private final BorderPane root;
    private final TextField searchField;
    private final VBox resultsContainer;
    private final Label statusLabel;
    private final ProgressIndicator spinner;

    public SearchView(SearchController controller) {
        this.controller = controller;

        root = new BorderPane();
        root.getStyleClass().add("search-root");

        // --- Top Bar ---
        Label backBtn = new Label("← Back");
        backBtn.getStyleClass().add("back-btn");
        backBtn.setCursor(Cursor.HAND);
        backBtn.setOnMouseClicked(e -> controller.goBack());

        Label title = new Label("Search Movies");
        title.getStyleClass().add("view-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(backBtn, spacer, title, spacer()); // center title visually
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(16, 32, 16, 32));
        root.setTop(topBar);

        // --- Search Bar ---
        searchField = new TextField();
        searchField.setPromptText("Search for a movie...");
        searchField.getStyleClass().add("search-field");
        searchField.setOnAction(e -> controller.performSearch(searchField.getText()));

        Label searchBtn = new Label("Search");
        searchBtn.getStyleClass().add("search-btn");
        searchBtn.setCursor(Cursor.HAND);
        searchBtn.setOnMouseClicked(e -> controller.performSearch(searchField.getText()));

        HBox searchBar = new HBox(12, searchField, searchBtn);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setPadding(new Insets(12, 32, 12, 32));
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // --- Status / Spinner ---
        spinner = new ProgressIndicator();
        spinner.setPrefSize(36, 36);
        spinner.setVisible(false);

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        HBox statusBar = new HBox(10, spinner, statusLabel);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(0, 32, 8, 32));

        // --- Results ---
        resultsContainer = new VBox(8);
        resultsContainer.setPadding(new Insets(0, 32, 32, 32));

        VBox centerContent = new VBox(searchBar, statusBar, resultsContainer);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("dashboard-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setCenter(scrollPane);
    }

    public void displayResults(List<TmdbMovie> movies) {
        resultsContainer.getChildren().clear();

        if (movies.isEmpty()) {
            statusLabel.setText("No results found.");
            return;
        }

        statusLabel.setText(movies.size() + " results");

        for (TmdbMovie movie : movies) {
            resultsContainer.getChildren().add(createResultRow(movie));
        }
    }

    private HBox createResultRow(TmdbMovie movie) {
        HBox row = new HBox(16);
        row.getStyleClass().add("search-result-row");
        row.setPadding(new Insets(12));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setCursor(Cursor.HAND);

        // Poster thumbnail
        ImageView poster = new ImageView();
        poster.setFitWidth(60);
        poster.setFitHeight(90);
        poster.setPreserveRatio(true);

        if (movie.getPosterUrl() != null) {
            Image img = new Image(movie.getPosterUrl(), 60, 90, true, true, true);
            poster.setImage(img);
        }

        // Text info
        Label titleLabel = new Label(movie.getTitle());
        titleLabel.getStyleClass().add("result-title");

        Label yearLabel = new Label(movie.getReleaseYear() > 0 ? String.valueOf(movie.getReleaseYear()) : "");
        yearLabel.getStyleClass().add("result-year");

        Label overviewLabel = new Label(truncate(movie.getOverview(), 120));
        overviewLabel.getStyleClass().add("result-overview");
        overviewLabel.setWrapText(true);
        overviewLabel.setMaxWidth(600);

        VBox textBox = new VBox(4, titleLabel, yearLabel, overviewLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        row.getChildren().addAll(poster, textBox);

        // Click to select
        row.setOnMouseClicked(e -> controller.onMovieSelected(movie));

        // Hover effect
        row.setOnMouseEntered(e -> row.getStyleClass().add("search-result-row-hover"));
        row.setOnMouseExited(e -> row.getStyleClass().remove("search-result-row-hover"));

        return row;
    }

    public void showLoading(boolean loading) {
        spinner.setVisible(loading);
        if (loading) {
            statusLabel.setText("Searching...");
        }
    }

    public void showError(String message) {
        statusLabel.setText("⚠ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    public void clearResults() {
        resultsContainer.getChildren().clear();
        statusLabel.setText("");
        statusLabel.setStyle("");
    }

    public void clearSearchField() {
        searchField.clear();
    }

    public Region getRoot() {
        return root;
    }

    private Region spacer() {
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        return s;
    }

    private String truncate(String text, int maxLen) {
        if (text == null)
            return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "…" : text;
    }
}
