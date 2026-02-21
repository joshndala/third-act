package com.thirdact.view;

import com.thirdact.controller.MainController;
import com.thirdact.service.ConfigManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Settings view — API keys + appearance theme.
 * Saves to ~/.thirdact/config.properties via ConfigManager.
 */
public class SettingsView {

    private final Region root;
    private final TextField tmdbField;
    private final PasswordField geminiFieldMasked;
    private final TextField geminiFieldVisible;
    private final Label statusLabel;

    private boolean geminiVisible = false;

    public SettingsView(Runnable onBack, MainController mainController) {

        // ── Top Bar ─────────────────────────────────────────────
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

        // ── Appearance Section ───────────────────────────────────
        Label appearanceLabel = new Label("Appearance");
        appearanceLabel.getStyleClass().add("settings-section-label");

        Label appearanceNote = new Label("Choose how The Third Act looks. \"System\" follows your macOS appearance.");
        appearanceNote.getStyleClass().add("settings-note");
        appearanceNote.setWrapText(true);

        // Segmented toggle: Dark | Light | System
        ToggleGroup themeGroup = new ToggleGroup();

        ToggleButton darkBtn = makeThemeToggle("🌙  Dark", "dark", themeGroup);
        ToggleButton lightBtn = makeThemeToggle("☀  Light", "light", themeGroup);
        ToggleButton systemBtn = makeThemeToggle("⬡  System", "system", themeGroup);

        // Pre-select current saved theme
        String savedTheme = ConfigManager.getInstance().getTheme();
        switch (savedTheme) {
            case "dark" -> darkBtn.setSelected(true);
            case "light" -> lightBtn.setSelected(true);
            default -> systemBtn.setSelected(true);
        }

        // Round the button group corners
        darkBtn.getStyleClass().add("theme-toggle-left");
        lightBtn.getStyleClass().add("theme-toggle-mid");
        systemBtn.getStyleClass().add("theme-toggle-right");

        HBox themeRow = new HBox(0, darkBtn, lightBtn, systemBtn);
        themeRow.setAlignment(Pos.CENTER_LEFT);

        // Live apply + persist on selection change
        themeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                // Prevent deselecting all — re-select previous
                if (oldToggle != null)
                    oldToggle.setSelected(true);
                return;
            }
            String mode = (String) newToggle.getUserData();
            mainController.applyTheme(mode);
            try {
                ConfigManager.getInstance().saveTheme(mode);
            } catch (IOException ex) {
                System.err.println("[SettingsView] Could not save theme: " + ex.getMessage());
            }
        });

        VBox appearanceSection = new VBox(12, appearanceLabel, appearanceNote, themeRow);

        // ── Divider ──────────────────────────────────────────────
        VBox divider = new VBox();
        divider.setStyle("-fx-background-color: #2B3358; -fx-min-height: 1; -fx-max-height: 1;");
        VBox.setMargin(divider, new Insets(8, 0, 8, 0));

        // ── API Keys Section ─────────────────────────────────────
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

        // Gemini Key (masked by default)
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

        // ── Assemble ─────────────────────────────────────────────
        VBox content = new VBox(24,
                appearanceSection,
                divider,
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

    private ToggleButton makeThemeToggle(String label, String mode, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(label);
        btn.setToggleGroup(group);
        btn.setUserData(mode);
        btn.getStyleClass().add("theme-toggle-btn");
        btn.setCursor(Cursor.HAND);
        btn.setPrefWidth(120);
        return btn;
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

            // Reset any warnings
            tmdbField.setStyle("");
            geminiFieldMasked.setStyle("");
            geminiFieldVisible.setStyle("");
        } catch (IOException ex) {
            statusLabel.setText("⚠ Could not save: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    public void showWarning(String message, boolean highlightTmdb, boolean highlightGemini) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #D4A15C; -fx-font-weight: bold;");

        if (highlightTmdb) {
            tmdbField.setStyle("-fx-border-color: #D4A15C; -fx-border-width: 2; -fx-border-radius: 4;");
        }
        if (highlightGemini) {
            geminiFieldMasked.setStyle("-fx-border-color: #D4A15C; -fx-border-width: 2; -fx-border-radius: 4;");
            geminiFieldVisible.setStyle("-fx-border-color: #D4A15C; -fx-border-width: 2; -fx-border-radius: 4;");
        }
    }

    public Region getRoot() {
        return root;
    }
}
