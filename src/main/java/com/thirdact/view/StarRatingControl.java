package com.thirdact.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Custom half-star rating control (0.0 – 5.0 in 0.5 increments).
 * Each star is split into left/right halves for half-star precision.
 */
public class StarRatingControl extends HBox {

    private static final int NUM_STARS = 5;
    private static final String STAR_SVG = "M12 .587l3.668 7.431 8.332 1.151-6.064 5.828 1.48 8.279L12 19.187l-7.416 4.089 1.48-8.279L0 9.169l8.332-1.151z";
    private static final double STAR_SIZE = 28;

    private double rating = 0.0;
    private final SVGPath[] stars = new SVGPath[NUM_STARS];
    private Runnable onRatingChanged;

    public StarRatingControl() {
        setSpacing(6);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(4, 0, 4, 0));
        setCursor(Cursor.HAND);

        for (int i = 0; i < NUM_STARS; i++) {
            SVGPath star = new SVGPath();
            star.setContent(STAR_SVG);
            star.setScaleX(STAR_SIZE / 24.0);
            star.setScaleY(STAR_SIZE / 24.0);
            star.setFill(Color.web("#3a3a3a"));
            star.setStroke(Color.web("#555555"));
            star.setStrokeWidth(0.5);

            final int starIndex = i;

            star.setOnMouseClicked(event -> {
                double localX = event.getX();
                double midpoint = 12; // SVG midpoint
                if (localX <= midpoint) {
                    setRating(starIndex + 0.5);
                } else {
                    setRating(starIndex + 1.0);
                }
            });

            star.setOnMouseEntered(event -> {
                star.setScaleX(STAR_SIZE / 24.0 * 1.15);
                star.setScaleY(STAR_SIZE / 24.0 * 1.15);
            });

            star.setOnMouseExited(event -> {
                star.setScaleX(STAR_SIZE / 24.0);
                star.setScaleY(STAR_SIZE / 24.0);
            });

            stars[i] = star;
            getChildren().add(star);
        }
    }

    /**
     * Sets the rating and updates star visuals.
     */
    public void setRating(double rating) {
        this.rating = Math.max(0, Math.min(5, rating));
        updateStars();
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

    private void updateStars() {
        for (int i = 0; i < NUM_STARS; i++) {
            double starValue = i + 1.0;
            if (rating >= starValue) {
                // Full star
                stars[i].setFill(Color.web("#d4a846"));
                stars[i].setStroke(Color.web("#d4a846"));
            } else if (rating >= starValue - 0.5) {
                // Half star — visually show as a dimmer gold
                stars[i].setFill(Color.web("#8a6e2e"));
                stars[i].setStroke(Color.web("#d4a846"));
            } else {
                // Empty star
                stars[i].setFill(Color.web("#3a3a3a"));
                stars[i].setStroke(Color.web("#555555"));
            }
        }
    }
}
