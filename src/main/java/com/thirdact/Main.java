package com.thirdact;

import com.thirdact.controller.MainController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.Taskbar;
import java.io.InputStream;

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
        // Set window title-bar icon (works in all environments)
        for (String iconName : new String[] { "icon.png", "icon.icns" }) {
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(iconName)) {
                if (stream != null) {
                    primaryStage.getIcons().add(new Image(stream));
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        // Initialize the main controller which sets up the scene and views
        new MainController(primaryStage);

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
        // Must be set before launch() for macOS to pick up the correct app name
        System.setProperty("apple.awt.application.name", "The Third Act");

        // Set macOS dock icon via AWT Taskbar — this works even when running
        // via `mvn javafx:run` (i.e. outside a packaged .app bundle).
        // Must happen before launch() initialises the JavaFX toolkit.
        try (InputStream iconStream = Main.class.getClassLoader().getResourceAsStream("icon.png")) {
            if (iconStream != null && Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    taskbar.setIconImage(ImageIO.read(iconStream));
                }
            }
        } catch (Exception ignored) {
            // Not on macOS, or icon file not present yet — silently skip
        }

        launch(args);
    }
}
