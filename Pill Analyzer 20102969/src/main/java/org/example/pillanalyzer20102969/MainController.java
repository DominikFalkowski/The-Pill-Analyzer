package org.example.pillanalyzer20102969;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class MainController {
    @FXML private Button uploadButton;
    @FXML
    Button BlackAndWhiteButton;
    @FXML
    Button DefaultButton;
    @FXML
    Button BlackAndColoredButton;
    @FXML
    Slider ThresholdSlider;
    @FXML
    ImageView secondaryImageView;
    @FXML
    ImageView primaryImageView;
    @FXML
    Window primaryStage;

    private Color lastClickedColor;
    private double lastThreshold = 0.3; // Default threshold
    private enum HighlightMode { DEFAULT, BLACK_AND_WHITE, BLACK_AND_COLORED }
    private HighlightMode currentMode = HighlightMode.DEFAULT;

    @FXML
    void initialize() {
        ThresholdSlider.setMin(0.0);
        ThresholdSlider.setMax(1.0);
        ThresholdSlider.setValue(0.5);
        ThresholdSlider.setShowTickLabels(true);
        ThresholdSlider.setShowTickMarks(true);
        ThresholdSlider.setMajorTickUnit(0.1);
        ThresholdSlider.setMinorTickCount(1);
        ThresholdSlider.setBlockIncrement(0.1);

        assert ThresholdSlider != null : "fx:id=\"ThresholdSlider\" was not injected: check your FXML file.";

        ThresholdSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Slider value changed to: " + newVal);
        });
        DefaultButton.setOnAction(this::handleBoundingBoxButton);
        BlackAndWhiteButton.setOnAction(this::handleBlackWhiteButton);
        BlackAndColoredButton.setOnAction(this::handleBlackColoredButton);
        primaryImageView.setOnMouseClicked(this::onPrimaryImageClick);
    }

    public void onUploadButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                primaryImageView.setImage(image);
                secondaryImageView.setImage(null);
            } catch (IOException e) {
                showAlert("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleBoundingBoxButton(ActionEvent event) {
        currentMode = HighlightMode.DEFAULT;
        if (lastClickedColor != null) {
            highlightClickedColorBoundingBox(lastClickedColor, lastThreshold);
        }
        updateButtonStates();
    }

    @FXML
    void handleBlackWhiteButton(ActionEvent event) {
        currentMode = HighlightMode.BLACK_AND_WHITE;
        if (lastClickedColor != null) {
            highlightClickedColorBlack(lastClickedColor, lastThreshold);
        }
        updateButtonStates();
    }

    @FXML
    private void handleBlackColoredButton(ActionEvent event) {
        currentMode = HighlightMode.BLACK_AND_COLORED;
        if (lastClickedColor != null) {
            highlightClickedColorBlackAndColored(lastClickedColor, lastThreshold);
        }
        updateButtonStates();
    }

    private void updateButtonStates() {
        DefaultButton.setDisable(currentMode == HighlightMode.DEFAULT);
        BlackAndWhiteButton.setDisable(currentMode == HighlightMode.BLACK_AND_WHITE);
        BlackAndColoredButton.setDisable(currentMode == HighlightMode.BLACK_AND_COLORED);
    }





    //Default
private void highlightClickedColorBoundingBox(Color clickedColor, double threshold) {
    Image originalImage = primaryImageView.getImage();
    int width = (int) originalImage.getWidth();
    int height = (int) originalImage.getHeight();

    // Initialize the color searcher for the specified dimensions
    ColorSearch colorSearch = new ColorSearch(width, height);

    // Search for bounding boxes
    Set<ColorSearch.BoundingBox> boundingBoxes = colorSearch.searchColorBoxes(originalImage, clickedColor, threshold);

    // Create a writable image and copy the original image data to it
    WritableImage highlightedImage = new WritableImage(width, height);
    PixelWriter pixelWriter = highlightedImage.getPixelWriter();
    PixelReader pixelReader = originalImage.getPixelReader();
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
        }
    }

    // Set the desired border thickness
    int borderThickness = 3; // Adjust this value to control the border size

    // Draw bounding boxes as borders on top of the original image
    for (ColorSearch.BoundingBox box : boundingBoxes) {
        // Top border
        for (int i = 0; i < borderThickness; i++) {
            for (int x = box.minX; x <= box.maxX; x++) {
                if (box.minY + i < height) {
                    pixelWriter.setColor(x, box.minY + i, Color.BLACK);
                }
            }
        }

        // Bottom border
        for (int i = 0; i < borderThickness; i++) {
            for (int x = box.minX; x <= box.maxX; x++) {
                if (box.maxY - i >= 0) {
                    pixelWriter.setColor(x, box.maxY - i, Color.BLACK);
                }
            }
        }

        // Left border
        for (int i = 0; i < borderThickness; i++) {
            for (int y = box.minY; y <= box.maxY; y++) {
                if (box.minX + i < width) {
                    pixelWriter.setColor(box.minX + i, y, Color.BLACK);
                }
            }
        }

        // Right border
        for (int i = 0; i < borderThickness; i++) {
            for (int y = box.minY; y <= box.maxY; y++) {
                if (box.maxX - i >= 0) {
                    pixelWriter.setColor(box.maxX - i, y, Color.BLACK);
                }
            }
        }
    }

    int numberOfMatchingRegions = boundingBoxes.size();
    System.out.println("Number of matching regions: " + numberOfMatchingRegions);


    Canvas canvas = new Canvas(width, height);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(highlightedImage, 0, 0);

    gc.setFill(Color.BLACK);
    gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    gc.fillText("Regions: " + numberOfMatchingRegions, 10, 30);

    WritableImage combinedImage = new WritableImage(width, height);
    canvas.snapshot(null, combinedImage);

    secondaryImageView.setImage(combinedImage);



}
                //Black and White
                private void highlightClickedColorBlack (Color clickedColor,double threshold){
                    Image originalImage = primaryImageView.getImage();
                    int width = (int) originalImage.getWidth();
                    int height = (int) originalImage.getHeight();

                    ColorSearch colorSearch = new ColorSearch(width, height);

                    Set<Point> matchingPixels = colorSearch.searchColorBlackBackground(originalImage, clickedColor, threshold);

                    WritableImage highlightedImage = new WritableImage(width, height);
                    fillWithBlack(highlightedImage);
                    PixelWriter pixelWriter = highlightedImage.getPixelWriter();

                    for (Point point : matchingPixels) {
                        pixelWriter.setColor(point.x, point.y, Color.WHITE);
                    }

                    secondaryImageView.setImage(highlightedImage);

                    System.out.println("Number of matching pixels: " + matchingPixels.size());
                }



                //Black and Colored
                @FXML
                private void highlightClickedColorBlackAndColored(Color clickedColor, double threshold) {
                    Image originalImage = primaryImageView.getImage();
                    int width = (int) originalImage.getWidth();
                    int height = (int) originalImage.getHeight();

                    ColorSearch colorSearch = new ColorSearch(width, height);
                    Set<Set<Point>> regions = colorSearch.searchColorBlackBackgroundColored(originalImage, clickedColor, threshold);

                    // Initialize a new writable image to hold the highlighted regions
                    WritableImage highlightedImage = new WritableImage(width, height);
                    PixelWriter pixelWriter = highlightedImage.getPixelWriter();
                    PixelReader pixelReader = originalImage.getPixelReader();

                    // Set the entire image to black initially
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            pixelWriter.setColor(x, y, Color.BLACK);
                        }
                    }

                    for (Set<Point> region : regions) {
                        for (Point point : region) {
                            Color originalColor = pixelReader.getColor(point.x, point.y);
                            pixelWriter.setColor(point.x, point.y, originalColor);
                        }
                    }

                    // Display the highlighted image in the secondary ImageView
                    secondaryImageView.setImage(highlightedImage);

                    // Print the number of matching regions
                    System.out.println("Number of matching regions: " + regions.size());
                }



    private void fillWithBlack (WritableImage image){
                    PixelWriter pixelWriter = image.getPixelWriter();
                    for (int y = 0; y < image.getHeight(); y++) {
                        for (int x = 0; x < image.getWidth(); x++) {
                            pixelWriter.setColor(x, y, Color.BLACK);
                        }
                    }
                }
    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onPrimaryImageClick(MouseEvent event) {
        Image image = primaryImageView.getImage();
        if (image != null) {
            PixelReader pixelReader = image.getPixelReader();
            int x = (int) (event.getX() * (image.getWidth() / primaryImageView.getBoundsInLocal().getWidth()));
            int y = (int) (event.getY() * (image.getHeight() / primaryImageView.getBoundsInLocal().getHeight()));
            Color clickedColor = pixelReader.getColor(x, y);
            lastClickedColor = clickedColor;
            lastThreshold = ThresholdSlider.getValue();

            Pill identifiedPill = PillIdentifier.identifyPill(clickedColor);
            System.out.printf("Clicked at (%d, %d): Pill identified as %s, Description: %s%n", x, y, identifiedPill.getName(), identifiedPill.getDescription());

            switch (currentMode) {
                case DEFAULT:
                    highlightClickedColorBoundingBox(clickedColor, lastThreshold);
                    break;
                case BLACK_AND_WHITE:
                    highlightClickedColorBlack(clickedColor, lastThreshold);
                    break;
                case BLACK_AND_COLORED:
                    highlightClickedColorBlackAndColored(clickedColor, lastThreshold);
                    break;
            }
        } else {
            System.out.println("No image is currently loaded in the primary ImageView.");
        }
    }

}
