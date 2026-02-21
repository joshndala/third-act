package com.thirdact.view;

import com.thirdact.controller.MainController;
import com.thirdact.dao.JournalEntryDAO;
import com.thirdact.model.JournalEntry;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The main dashboard view displaying journal entries as cinematic backdrop
 * cards
 * and a cinema tracker widget.
 */
public class DashboardView {

    private final MainController mainController;
    private final BorderPane root;
    private final FlowPane entriesGrid;
    private final Label trackerLabel;

    // Custom progress bar built from StackPane + Region
    private final StackPane trackerBarContainer;
    private final Region trackerBarFill;

    private final JournalEntryDAO dao;

    public DashboardView(MainController mainController) {
        this.mainController = mainController;
        this.dao = new JournalEntryDAO();

        root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        // --- Top Bar ---
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // --- Cinema Tracker ---
        trackerLabel = new Label("Cinema Goal: 0 / 2 this month");
        trackerLabel.getStyleClass().add("tracker-label");

        trackerBarFill = new Region();
        trackerBarFill.getStyleClass().add("tracker-bar-fill");
        trackerBarFill.setMinHeight(8);
        trackerBarFill.setMaxHeight(8);

        trackerBarContainer = new StackPane();
        trackerBarContainer.getStyleClass().add("tracker-bar-container");
        trackerBarContainer.setMinHeight(8);
        trackerBarContainer.setMaxHeight(8);
        trackerBarContainer.setAlignment(Pos.CENTER_LEFT);
        trackerBarContainer.getChildren().add(trackerBarFill);

        VBox trackerBox = new VBox(6, trackerLabel, trackerBarContainer);
        trackerBox.getStyleClass().add("tracker-box");
        trackerBox.setPadding(new Insets(16, 32, 8, 32));

        // --- Entries Grid ---
        entriesGrid = new FlowPane();
        entriesGrid.getStyleClass().add("entries-grid");
        entriesGrid.setHgap(20);
        entriesGrid.setVgap(20);
        entriesGrid.setPadding(new Insets(20, 32, 32, 32));
        entriesGrid.setAlignment(Pos.TOP_LEFT);

        VBox contentBox = new VBox(trackerBox, entriesGrid);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("dashboard-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setCenter(scrollPane);
    }

    private HBox createTopBar() {
        Label appTitle = new Label("THE THIRD ACT");
        appTitle.getStyleClass().add("app-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label newEntryBtn = new Label("+ New Entry");
        newEntryBtn.getStyleClass().add("new-entry-btn");
        newEntryBtn.setCursor(Cursor.HAND);
        newEntryBtn.setOnMouseClicked(e -> mainController.showSearch());

        Label settingsBtn = new Label("⚙");
        settingsBtn.getStyleClass().add("settings-btn");
        settingsBtn.setCursor(Cursor.HAND);
        settingsBtn.setOnMouseClicked(e -> mainController.showSettings());

        HBox topBar = new HBox(16, appTitle, spacer, newEntryBtn, settingsBtn);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(16, 32, 16, 32));

        return topBar;
    }

    /**
     * Refreshes the dashboard data from the database on a background thread.
     */
    public void refresh() {
        Task<DashboardData> loadTask = new Task<>() {
            @Override
            protected DashboardData call() throws Exception {
                List<JournalEntry> entries = dao.getAllEntries();
                LocalDate now = LocalDate.now();
                int theaterCount = dao.getTheaterCountForMonth(now.getYear(), now.getMonthValue());
                return new DashboardData(entries, theaterCount);
            }
        };

        loadTask.setOnSucceeded(event -> Platform.runLater(() -> {
            DashboardData data = loadTask.getValue();
            updateTracker(data.theaterCount);
            updateGrid(data.entries);
        }));

        loadTask.setOnFailed(event -> {
            loadTask.getException().printStackTrace();
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateTracker(int theaterCount) {
        String monthName = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM"));
        trackerLabel.setText("🎬  Cinema Goal: " + theaterCount + " / 2 in " + monthName);

        double progress = Math.min(theaterCount / 2.0, 1.0);
        trackerBarFill.setMaxWidth(trackerBarContainer.getWidth() * progress);

        // Re-bind on layout
        trackerBarContainer.widthProperty().addListener((obs, old, newVal) -> {
            trackerBarFill.setMaxWidth(newVal.doubleValue() * progress);
        });
    }

    private void updateGrid(List<JournalEntry> entries) {
        entriesGrid.getChildren().clear();

        if (entries.isEmpty()) {
            Label emptyLabel = new Label("No entries yet. Tap \"+ New Entry\" to start your journal.");
            emptyLabel.getStyleClass().add("empty-label");
            entriesGrid.getChildren().add(emptyLabel);
            return;
        }

        for (JournalEntry entry : entries) {
            entriesGrid.getChildren().add(createEntryCard(entry));
        }
    }

    private StackPane createEntryCard(JournalEntry entry) {
        double cardWidth = 320;
        double cardHeight = 180;

        StackPane card = new StackPane();
        card.setPrefSize(cardWidth, cardHeight);
        card.setMaxSize(cardWidth, cardHeight);
        card.setMinSize(cardWidth, cardHeight);
        card.getStyleClass().add("entry-card");
        card.setCursor(Cursor.HAND);

        // Clip rounded corners
        Rectangle clip = new Rectangle(cardWidth, cardHeight);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        card.setClip(clip);

        // Backdrop image
        if (entry.getBackdropUrl() != null && !entry.getBackdropUrl().isBlank()) {
            ImageView backdrop = new ImageView();
            backdrop.setFitWidth(cardWidth);
            backdrop.setFitHeight(cardHeight);
            backdrop.setPreserveRatio(false);

            Image img = new Image(entry.getBackdropUrl(), true); // load at native res, background thread
            backdrop.setImage(img);
            card.getChildren().add(backdrop);
        }

        // Gradient overlay
        Region overlay = new Region();
        overlay.getStyleClass().add("card-overlay");
        overlay.setPrefSize(cardWidth, cardHeight);
        card.getChildren().add(overlay);

        // Text content
        Label titleLabel = new Label(entry.getTitle());
        titleLabel.getStyleClass().add("card-title");

        String yearAndRating = entry.getReleaseYear() + "  •  " + formatRating(entry.getRating());
        Label detailLabel = new Label(yearAndRating);
        detailLabel.getStyleClass().add("card-detail");

        VBox textBox = new VBox(4, titleLabel, detailLabel);
        textBox.setAlignment(Pos.BOTTOM_LEFT);
        textBox.setPadding(new Insets(12, 16, 14, 16));

        StackPane.setAlignment(textBox, Pos.BOTTOM_LEFT);
        card.getChildren().add(textBox);

        // Shadow
        card.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.5)));

        // Click to edit
        card.setOnMouseClicked(e -> mainController.showEntryFormForEdit(entry));

        // Hover animation
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.03);
            card.setScaleY(1.03);
        });
        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });

        return card;
    }

    private String formatRating(double rating) {
        if (rating == (int) rating) {
            return "★".repeat((int) rating);
        }
        return "★".repeat((int) rating) + "½";
    }

    public Region getRoot() {
        return root;
    }

    // Simple data holder
    private record DashboardData(List<JournalEntry> entries, int theaterCount) {
    }
}
