package org.example.pillanalyzer20102969;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainControllerTest {
    private Stage primaryStage;
    private ImageView primaryImageView, secondaryImageView;
    private Slider ThresholdSlider;
    private Button DefaultButton, BlackAndWhiteButton, BlackAndColoredButton;

    private MainController controller;

    @BeforeEach
    void setUp() {
        new JFXPanel(); // Initializes the JavaFX environment
        Platform.runLater(() -> {
            primaryStage = new Stage();
            primaryImageView = new ImageView();
            secondaryImageView = new ImageView();
            ThresholdSlider = new Slider();
            DefaultButton = new Button();
            BlackAndWhiteButton = new Button();
            BlackAndColoredButton = new Button();

            controller = new MainController();
            controller.primaryStage = primaryStage;
            controller.primaryImageView = primaryImageView;
            controller.secondaryImageView = secondaryImageView;
            controller.ThresholdSlider = ThresholdSlider;
            controller.DefaultButton = DefaultButton;
            controller.BlackAndWhiteButton = BlackAndWhiteButton;
            controller.BlackAndColoredButton = BlackAndColoredButton;

            controller.initialize(); // Manually call initialize to set up components
        });
    }

    @Test
    void testButtonStateAfterModeSelection() throws Exception {
        Platform.runLater(() -> {
            controller.handleBoundingBoxButton(null);
            assertTrue(DefaultButton.isDisabled(), "Default button should be disabled after being clicked.");

            controller.handleBlackWhiteButton(null);
            assertTrue(BlackAndWhiteButton.isDisabled(), "Black and White button should be disabled after being clicked.");
        });
    }
}
