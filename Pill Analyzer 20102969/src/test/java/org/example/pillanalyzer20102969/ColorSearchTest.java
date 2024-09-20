package org.example.pillanalyzer20102969;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ColorSearchTest {
    @Test
    void testColorDistance() {
        ColorSearch colorSearch = new ColorSearch(10, 10); // Size doesn't matter for this test
        Color c1 = Color.rgb(255, 0, 0, 1); // Red
        Color c2 = Color.rgb(0, 255, 0, 1); // Green

        // Expected distance calculation
        double redDiff = c1.getRed() - c2.getRed();
        double greenDiff = c1.getGreen() - c2.getGreen();
        double blueDiff = c1.getBlue() - c2.getBlue();
        double expectedDistance = Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);

        double calculatedDistance = colorSearch.colorDistance(c1, c2);
        assertEquals(expectedDistance, calculatedDistance, 0.01, "Color distance should be calculated correctly.");
    }
}