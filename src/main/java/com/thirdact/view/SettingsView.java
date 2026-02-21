package com.thirdact.view;

import com.thirdact.service.ConfigManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Settings view where users can view and update their API keys.
 * Saves to ~/.thirdact/config.properties via ConfigManager.
 */
public class SettingsView {

    private final Region root;
    private final TextField tmdbField;
    private final PasswordField geminiFieldMasked;
    private final TextField geminiFieldVisible;
    private final Label statusLabel;

    private boolean geminiVisible = false;

    public SettingsView(Runnable onBack) {

        // --- Top Bar ---
        Label backBtn = new Label("← Back");
        backBtn.getStyleClass().add("back-btn");
        backBtn.setCursor(Cursor.HAND);
        backBtn.setOnMouseClicked(e -> onBack.run());

        Label title = new Label("Settings");
        title.getStyleClass().add("view-title");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox topBar = new HBox(backBtn, spacer1, title, spacer2);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(16, 32, 16, 32));

        // --- API Keys Section ---
        Label sectionLabel = new Label("API Keys");
        sectionLabel.getStyleClass().add("settings-section-label");

        Label sectionNote = new Label("Your keys are stored locally in ~/.thirdact/config.properties — never shared.");
        sectionNote.getStyleClass().add("settings-note");
        sectionNote.setWrapText(true);

        // TMDB Key
        Label tmdbLabel = new Label("TMDb API Key");
        tmdbLabel.getStyleClass().add("field-label");
        tmdbField = new TextField(ConfigManager.getInstance().getTmdbApiKey());
        tmdbField.getStyleClass().add("settings-field");
        tmdbField.setPromptText("Paste your TMDb API key here…");

        // Gemini Key (masked by default with show/hide toggle)
        Label geminiLabel = new Label("Gemini API Key");
        geminiLabel.getStyleClass().add("field-label");

        geminiFieldMasked = new PasswordField();
        geminiFieldMasked.getStyleClass().add("settings-field");
        geminiFieldMasked.setPromptText("Paste your Gemini API key here…");
        geminiFieldMasked.setText(ConfigManager.getInstance().getGeminiApiKey());

        geminiFieldVisible = new TextField();
        geminiFieldVisible.getStyleClass().add("settings-field");
        geminiFieldVisible.setPromptText("Paste your Gemini API key here…");
        geminiFieldVisible.setVisible(false);
        geminiFieldVisible.setManaged(false);

        Label toggleVisibility = new Label("Show");
        toggleVisibility.getStyleClass().add("show-hide-btn");
        toggleVisibility.setCursor(Cursor.HAND);
        toggleVisibility.setOnMouseClicked(e -> {
            geminiVisible = !geminiVisible;
            if (geminiVisible) {
                geminiFieldVisible.setText(geminiFieldMasked.getText());
                geminiFieldMasked.setVisible(false);
                geminiFieldMasked.setManaged(false);
                geminiFieldVisible.setVisible(true);
                geminiFieldVisible.setManaged(true);
                toggleVisibility.setText("Hide");
            } else {
                geminiFieldMasked.setText(geminiFieldVisible.getText());
                geminiFieldVisible.setVisible(false);
                geminiFieldVisible.setManaged(false);
                geminiFieldMasked.setVisible(true);
                geminiFieldMasked.setManaged(true);
                toggleVisibility.setText("Show");
            }
        });

        StackPane geminiFieldStack = new StackPane(geminiFieldMasked, geminiFieldVisible);
        HBox.setHgrow(geminiFieldStack, Priority.ALWAYS);

        HBox geminiRow = new HBox(10, geminiFieldStack, toggleVisibility);
        geminiRow.setAlignment(Pos.CENTER_LEFT);

        // Status + Save button
        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        Label saveBtn = new Label("Save Keys");
        saveBtn.getStyleClass().add("settings-save-btn");
        saveBtn.setCursor(Cursor.HAND);
        saveBtn.setOnMouseClicked(e -> onSave());

        HBox saveRow = new HBox(16, saveBtn, statusLabel);
        saveRow.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(24,
                sectionLabel,
                sectionNote,
                new VBox(8, tmdbLabel, tmdbField),
                new VBox(8, geminiLabel, geminiRow),
                saveRow);
        content.setPadding(new Insets(32, 40, 40, 40));
        content.setMaxWidth(700);

        StackPane wrapper = new StackPane(content);
        wrapper.setAlignment(Pos.TOP_CENTER);

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("settings-root");
        borderPane.setTop(topBar);
        borderPane.setCenter(wrapper);

        root = borderPane;
    }

    private void onSave() {
        String tmdbKey = tmdbField.getText().trim();
        String geminiKey = geminiVisible
                ? geminiFieldVisible.getText().trim()
                : geminiFieldMasked.getText().trim();

        try {
            ConfigManager.getInstance().saveKeys(
                    tmdbKey.isEmpty() ? null : tmdbKey,
                    geminiKey.isEmpty() ? null : geminiKey);
            statusLabel.setText("✓ Keys saved.");
            statusLabel.setStyle("-fx-text-fill: #4caf50;");
        } catch (IOException ex) {
            statusLabel.setText("⚠ Could not save: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    public Region getRoot() {
        return root;
    }
}
