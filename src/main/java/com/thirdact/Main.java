package com.thirdact;

import com.thirdact.controller.MainController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The Third Act — A cinematic movie journaling application.
 *
 * Entry point for the JavaFX application.
 * Bootstraps the main stage, applies the dark cinematic theme,
 * and hands off control to MainController.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Set application icon (if available)
        try {
            primaryStage.getIcons().add(
                    new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
        } catch (Exception ignored) {
            // No icon bundled yet — proceed without
        }

        // Initialize the main controller which sets up the scene and views
        MainController mainController = new MainController(primaryStage);

        // Show the stage
        primaryStage.show();

        System.out.println("[The Third Act] Application started.");
    }

    @Override
    public void stop() {
        System.out.println("[The Third Act] Application shutting down.");
        // DatabaseManager close is handled by MainController's onCloseRequest
    }

    public static void main(String[] args) {
        launch(args);
    }
}
