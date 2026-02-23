package com.thirdact.view;

import com.thirdact.controller.EntryController;
import com.thirdact.model.JournalEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Entry form view for creating or editing a journal entry.
 * Pure JavaFX code — no FXML.
 */
public class EntryFormView {

    private final EntryController controller;
    private final StackPane mainWrapper;
    private final BorderPane root;
    private VBox qrOverlay;

    // Form fields
    private final Label movieTitleLabel;
    private final Label movieYearLabel;
    private final ImageView posterView;
    private final StarRatingControl ratingControl;
    private final TextArea summaryArea;
    private final TextArea vibeArea;
    private final TextArea peakMomentArea;
    private final TextArea extraNotesArea;
    private final CheckBox theaterToggle;
    private final DatePicker watchDatePicker;
    private final Label saveBtn;
    private final Label cancelBtn;
    private final Label deleteBtn;
    private final Label statusLabel;

    // Import from notes
    private final Label importBtn;
    private final Label mobileBtn;
    private final Label importStatusLabel;

    // Stored metadata
    private int tmdbId;
    private String posterUrl;
    private String backdropUrl;

    public EntryFormView(EntryController controller) {
        this.controller = controller;

        root = new BorderPane();
        root.getStyleClass().add("entry-form-root");
        mainWrapper = new StackPane(root);

        // --- Top Bar ---
        Label backBtn = new Label("← Cancel");
        backBtn.getStyleClass().add("back-btn");
        backBtn.setCursor(Cursor.HAND);
        backBtn.setOnMouseClicked(e -> controller.cancel());

        Label title = new Label("Journal Entry");
        title.getStyleClass().add("view-title");

        HBox topBar = new HBox(backBtn, spacer(), title, spacer());
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(16, 32, 16, 32));
        root.setTop(topBar);

        // --- Movie Header ---
        posterView = new ImageView();
        posterView.setFitWidth(120);
        posterView.setFitHeight(180);
        posterView.setPreserveRatio(true);

        movieTitleLabel = new Label();
        movieTitleLabel.getStyleClass().add("entry-movie-title");
        movieTitleLabel.setWrapText(true);

        movieYearLabel = new Label();
        movieYearLabel.getStyleClass().add("entry-movie-year");

        // Rating
        Label ratingLabel = new Label("Your Rating");
        ratingLabel.getStyleClass().add("field-label");
        ratingControl = new StarRatingControl();

        VBox ratingBox = new VBox(4, ratingLabel, ratingControl);

        VBox movieInfo = new VBox(8, movieTitleLabel, movieYearLabel, ratingBox);
        movieInfo.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(movieInfo, Priority.ALWAYS);

        HBox headerBox = new HBox(20, posterView, movieInfo);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        // --- Import from Notes Button ---
        importBtn = new Label("📎  Import from Handwritten Notes");
        importBtn.getStyleClass().add("import-btn");
        importBtn.setCursor(Cursor.HAND);
        importBtn.setOnMouseClicked(e -> onImport());

        mobileBtn = new Label("📱 Scan from Mobile");
        mobileBtn.getStyleClass().add("import-btn");
        mobileBtn.setCursor(Cursor.HAND);
        mobileBtn.setOnMouseClicked(e -> controller.startMobileUpload());

        importStatusLabel = new Label();
        importStatusLabel.getStyleClass().add("import-status-label");

        HBox importRow = new HBox(16, importBtn, mobileBtn, importStatusLabel);
        importRow.setAlignment(Pos.CENTER_LEFT);

        // --- Text Areas ---
        summaryArea = createTextArea("Tell a friend — what's this movie about?", 3);
        vibeArea = createTextArea("What did it feel like to watch this?", 4);
        peakMomentArea = createTextArea("The single most impactful scene or moment...", 6);
        extraNotesArea = createTextArea("Any extra thoughts, trivia, or context...", 4);

        VBox fieldsBox = new VBox(16,
                importRow,
                labeledField("The Summary", summaryArea),
                labeledField("The Vibe", vibeArea),
                labeledField("The Peak Moment", peakMomentArea),
                labeledField("Extra Notes", extraNotesArea));

        // --- Bottom Row: Theater toggle, Date, Save ---
        theaterToggle = new CheckBox("Watched in Theaters 🎬");
        theaterToggle.getStyleClass().add("theater-toggle");

        watchDatePicker = new DatePicker(LocalDate.now());
        watchDatePicker.getStyleClass().add("watch-date-picker");

        HBox optionsRow = new HBox(24, theaterToggle, watchDatePicker);
        optionsRow.setAlignment(Pos.CENTER_LEFT);

        saveBtn = new Label("Save Entry");
        saveBtn.getStyleClass().add("save-btn");
        saveBtn.setCursor(Cursor.HAND);
        saveBtn.setOnMouseClicked(e -> onSave());

        cancelBtn = new Label("Cancel");
        cancelBtn.getStyleClass().add("cancel-btn");
        cancelBtn.setCursor(Cursor.HAND);
        cancelBtn.setOnMouseClicked(e -> controller.cancel());

        deleteBtn = new Label("Delete");
        deleteBtn.getStyleClass().add("cancel-btn");
        deleteBtn.setStyle("-fx-text-fill: #e74c3c;");
        deleteBtn.setCursor(Cursor.HAND);
        deleteBtn.setOnMouseClicked(e -> onDelete());
        deleteBtn.setVisible(false);
        deleteBtn.managedProperty().bind(deleteBtn.visibleProperty());

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        HBox actionRow = new HBox(16, saveBtn, cancelBtn, deleteBtn, statusLabel);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox formContent = new VBox(20, headerBox, fieldsBox, optionsRow, actionRow);
        formContent.setPadding(new Insets(24, 40, 40, 40));
        formContent.setMaxWidth(800);

        StackPane formWrapper = new StackPane(formContent);
        formWrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(formWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("dashboard-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setCenter(scrollPane);
    }

    /**
     * Populates the form with entry data.
     */
    public void populateForm(JournalEntry entry, boolean isEdit) {
        tmdbId = entry.getTmdbId();
        posterUrl = entry.getPosterUrl();
        backdropUrl = entry.getBackdropUrl();

        movieTitleLabel.setText(entry.getTitle());
        movieYearLabel.setText(entry.getReleaseYear() > 0 ? String.valueOf(entry.getReleaseYear()) : "");

        // Load poster
        if (posterUrl != null && !posterUrl.isBlank()) {
            posterView.setImage(new Image(posterUrl, true)); // native res, background load
        } else {
            posterView.setImage(null);
        }

        ratingControl.setRating(entry.getRating());
        summaryArea.setText(entry.getSummary() != null ? entry.getSummary() : "");
        vibeArea.setText(entry.getVibe() != null ? entry.getVibe() : "");
        peakMomentArea.setText(entry.getPeakMoment() != null ? entry.getPeakMoment() : "");
        extraNotesArea.setText(entry.getExtraNotes() != null ? entry.getExtraNotes() : "");
        theaterToggle.setSelected(entry.isWatchedInTheaters());

        if (entry.getWatchDate() != null && !entry.getWatchDate().isBlank()) {
            try {
                watchDatePicker.setValue(LocalDate.parse(entry.getWatchDate()));
            } catch (Exception e) {
                watchDatePicker.setValue(LocalDate.now());
            }
        } else {
            watchDatePicker.setValue(LocalDate.now());
        }

        saveBtn.setText(isEdit ? "Update Entry" : "Save Entry");
        deleteBtn.setVisible(isEdit);
        statusLabel.setText("");
        importStatusLabel.setText("");
        importBtn.setDisable(false);
        mobileBtn.setDisable(false);
    }

    /**
     * Shows saving state.
     */
    public void setSaving(boolean saving) {
        saveBtn.setDisable(saving);
        statusLabel.setText(saving ? "Saving..." : "");
    }

    /**
     * Shows/hides the importing state while Gemini processes the files.
     */
    public void setImporting(boolean importing) {
        importBtn.setDisable(importing);
        mobileBtn.setDisable(importing);
        saveBtn.setDisable(importing);
        deleteBtn.setDisable(importing);

        // Freeze inputs but don't freeze the whole root (Cancel should stay clickable)
        summaryArea.setDisable(importing);
        vibeArea.setDisable(importing);
        peakMomentArea.setDisable(importing);
        extraNotesArea.setDisable(importing);
        theaterToggle.setDisable(importing);
        watchDatePicker.setDisable(importing);
        ratingControl.setDisable(importing);

        importStatusLabel.setStyle("");
        importStatusLabel.setText(importing ? "⏳  Analysing with Gemini…" : "");

        // Also clear any previous general errors
        if (importing) {
            statusLabel.setText("");
        }
    }

    /**
     * Fills the text areas with AI-extracted content, word-for-word.
     * The user can still edit before saving.
     */
    public void fillFromAI(Map<String, String> result) {
        statusLabel.setText(""); // clear the general error label if any
        setTextIfNotEmpty(summaryArea, result.get("summary"));
        setTextIfNotEmpty(vibeArea, result.get("vibe"));
        setTextIfNotEmpty(peakMomentArea, result.get("peakMoment"));
        setTextIfNotEmpty(extraNotesArea, result.get("extraNotes"));
        importStatusLabel.setStyle("-fx-text-fill: #4caf50;");
        importStatusLabel.setText("✓  Notes imported — feel free to edit before saving.");
    }

    /**
     * Shows an error message below the form.
     */
    public void showError(String message) {
        statusLabel.setText("⚠ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    public Region getRoot() {
        return mainWrapper;
    }

    /**
     * Shows an overlay with a QR code for mobile upload.
     */
    public void showQrPopup(Image qrImage, String url, Runnable onCancel) {
        if (qrOverlay != null)
            hideQrPopup();

        qrOverlay = new VBox(20);
        qrOverlay.setAlignment(Pos.CENTER);
        qrOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

        Label title = new Label("Scan to Upload Note");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label instruction = new Label("1. Join your phone to the same Wi-Fi.\n" +
                "2. Scan this QR code with your Camera app.\n" +
                "3. Take or select a photo on your phone.");
        instruction.setStyle("-fx-font-size: 16px; -fx-text-fill: #cccccc;");
        instruction.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        ImageView qrView = new ImageView(qrImage);
        qrView.setFitWidth(250);
        qrView.setFitHeight(250);

        Label urlLabel = new Label(url);
        urlLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-text-fill: #888888;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-font-size: 16px; -fx-padding: 8px 20px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> {
            hideQrPopup();
            onCancel.run();
        });

        qrOverlay.getChildren().addAll(title, instruction, qrView, urlLabel, cancelBtn);

        // Bind overlay to match root size
        qrOverlay.prefWidthProperty().bind(mainWrapper.widthProperty());
        qrOverlay.prefHeightProperty().bind(mainWrapper.heightProperty());

        mainWrapper.getChildren().add(qrOverlay);
    }

    /**
     * Hides the QR code overlay.
     */
    public void hideQrPopup() {
        if (qrOverlay != null) {
            mainWrapper.getChildren().remove(qrOverlay);
            qrOverlay = null;
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Handwritten Notes (Images or PDF)");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images & PDFs",
                        "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif", "*.pdf"),
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        Window window = root.getScene() != null ? root.getScene().getWindow() : null;
        List<File> files = chooser.showOpenMultipleDialog(window);

        if (files != null && !files.isEmpty()) {
            importStatusLabel.setStyle("");
            importStatusLabel.setText("");
            controller.importFromFiles(files);
        }
    }

    private void onDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Entry");
        alert.setHeaderText("Delete Movie Log");
        alert.setContentText("Are you sure? This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.deleteEntry();
            }
        });
    }

    private void onSave() {
        JournalEntry entry = new JournalEntry()
                .setTmdbId(tmdbId)
                .setTitle(movieTitleLabel.getText())
                .setReleaseYear(parseYear(movieYearLabel.getText()))
                .setPosterUrl(posterUrl)
                .setBackdropUrl(backdropUrl)
                .setRating(ratingControl.getRating())
                .setSummary(summaryArea.getText())
                .setVibe(vibeArea.getText())
                .setPeakMoment(peakMomentArea.getText())
                .setExtraNotes(extraNotesArea.getText())
                .setWatchedInTheaters(theaterToggle.isSelected())
                .setWatchDate(watchDatePicker.getValue() != null
                        ? watchDatePicker.getValue().toString()
                        : LocalDate.now().toString());

        controller.saveEntry(entry);
    }

    private VBox labeledField(String labelText, TextArea area) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        return new VBox(6, label, area);
    }

    private TextArea createTextArea(String prompt, int rows) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setPrefRowCount(rows);
        area.setWrapText(true);
        area.getStyleClass().add("journal-text-area");
        return area;
    }

    private void setTextIfNotEmpty(TextArea area, String text) {
        if (text != null && !text.isBlank()) {
            area.setText(text);
        }
    }

    private Region spacer() {
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        return s;
    }

    private int parseYear(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }
}
