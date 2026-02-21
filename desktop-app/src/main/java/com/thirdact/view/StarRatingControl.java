package com.thirdact.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;

/**
 * Custom half-star rating control (0.0 – 5.0 in 0.5 increments).
 *
 * Each star is a single SVGPath whose fill switches between three states:
 * • Full — solid gold fill
 * • Half — LinearGradient: gold on left 50%, dark on right 50%
 * • Empty — solid dark fill
 *
 * This produces a clean, pixel-perfect half-star without any clipping hacks.
 *
 * Supports hover preview: moving the cursor over the stars previews the
 * would-be rating; leaving the control snaps back to the committed value.
 */
public class StarRatingControl extends HBox {

    private static final int NUM_STARS = 5;
    private static final double STAR_SIZE = 30;
    private static final double SCALE = STAR_SIZE / 24.0;

    private static final String STAR_PATH = "M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z";

    // Colours
    private static final Color GOLD = Color.web("#d4a846");
    private static final Color DARK = Color.web("#2e2e2e");
    private static final String STROKE_GOLD = "#d4a846";
    private static final String STROKE_DARK = "#555555";

    // Half-star gradient: gold [0%–50%] → dark [50%–100%]
    private static final LinearGradient HALF_GRAD = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, GOLD),
            new Stop(0.5, GOLD),
            new Stop(0.5, DARK),
            new Stop(1.0, DARK));

    private double rating = 0.0;
    private double hoverRating = -1; // -1 = not hovering
    private Runnable onRatingChanged;

    private final SVGPath[] stars = new SVGPath[NUM_STARS];

    public StarRatingControl() {
        setSpacing(4);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(4, 0, 4, 0));
        setCursor(Cursor.HAND);

        for (int i = 0; i < NUM_STARS; i++) {
            final int idx = i;

            SVGPath star = new SVGPath();
            star.setContent(STAR_PATH);
            star.setScaleX(SCALE);
            star.setScaleY(SCALE);
            star.setFill(DARK);
            star.setStroke(Color.web(STROKE_DARK));
            star.setStrokeWidth(0.8);

            // --- Hover preview ---
            star.setOnMouseMoved(event -> {
                double mid = star.getBoundsInLocal().getWidth() / 2.0;
                hoverRating = event.getX() <= mid ? idx + 0.5 : idx + 1.0;
                render(hoverRating);
            });

            // --- Commit on click ---
            star.setOnMouseClicked(event -> {
                double mid = star.getBoundsInLocal().getWidth() / 2.0;
                setRating(event.getX() <= mid ? idx + 0.5 : idx + 1.0);
            });

            stars[i] = star;
            getChildren().add(star);
        }

        // Revert to committed rating when cursor leaves the control
        setOnMouseExited(e -> {
            hoverRating = -1;
            render(rating);
        });
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void setRating(double value) {
        this.rating = Math.max(0, Math.min(5, value));
        this.hoverRating = -1;
        render(rating);
        if (onRatingChanged != null) {
            onRatingChanged.run();
        }
    }

    public double getRating() {
        return rating;
    }

    public void setOnRatingChanged(Runnable callback) {
        this.onRatingChanged = callback;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    private void render(double displayValue) {
        for (int i = 0; i < NUM_STARS; i++) {
            double threshold = i + 1.0;
            SVGPath star = stars[i];

            if (displayValue >= threshold) {
                // Full star — solid gold
                star.setFill(GOLD);
                star.setStroke(Color.web(STROKE_GOLD));
            } else if (displayValue >= threshold - 0.5) {
                // Half star — left gold, right dark
                star.setFill(HALF_GRAD);
                star.setStroke(Color.web(STROKE_DARK));
            } else {
                // Empty star — solid dark
                star.setFill(DARK);
                star.setStroke(Color.web(STROKE_DARK));
            }
        }
    }
}
