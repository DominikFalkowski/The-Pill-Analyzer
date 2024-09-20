package org.example.pillanalyzer20102969;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.awt.Point;
import java.util.*;

public class ColorSearch {
    private final int[] parent;
    private final BoundingBox[] boundingBoxes; // Array to store bounding boxes for each root

    public ColorSearch(int width, int height) {
        int imageSize = width * height;
        parent = new int[imageSize];
        boundingBoxes = new BoundingBox[imageSize];
        int[] size = new int[imageSize];

        for (int i = 0; i < imageSize; i++) {
            parent[i] = i;
            size[i] = 1;
            boundingBoxes[i] = new BoundingBox(i % width, i / width); // Initialize bounding boxes
        }
    }

    private int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]); // Path compression
        }
        return parent[i];
    }

    private void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI != rootJ) {
            parent[rootI] = rootJ;

            boundingBoxes[rootJ].update(boundingBoxes[rootI]);
        }
    }

    double colorDistance(Color c1, Color c2) {
//        return Math.abs(c1.getHue()-c2.getHue())<10 ? 0 : 1;
        if (c1 == null || c2 == null) {
            return Double.MAX_VALUE;
        }

        double redDiff = c1.getRed() - c2.getRed();
        double greenDiff = c1.getGreen() - c2.getGreen();
        double blueDiff = c1.getBlue() - c2.getBlue();

        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    public Set<BoundingBox> searchColorBoxes(Image image, Color targetColor, double threshold) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        Set<Integer> roots = new HashSet<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelIndex = y * width + x;
                Color pixelColor = pixelReader.getColor(x, y);
                if (colorDistance(pixelColor, targetColor) <= threshold) {
                    int root = find(pixelIndex);
                    roots.add(root);

                    // Perform unions with neighboring pixels within threshold
                    if (x > 0 && colorDistance(pixelReader.getColor(x - 1, y), targetColor) <= threshold) {
                        union(pixelIndex, pixelIndex - 1);
                    }
                    if (x < width - 1 && colorDistance(pixelReader.getColor(x + 1, y), targetColor) <= threshold) {
                        union(pixelIndex, pixelIndex + 1);
                    }
                    if (y > 0 && colorDistance(pixelReader.getColor(x, y - 1), targetColor) <= threshold) {
                        union(pixelIndex, pixelIndex - width);
                    }
                    if (y < height - 1 && colorDistance(pixelReader.getColor(x, y + 1), targetColor) <= threshold) {
                        union(pixelIndex, pixelIndex + width);
                    }
                }
            }
        }

        // Collect bounding boxes for each connected component root
        Map<Integer, BoundingBox> boxes = new HashMap<>();
        for (int i = 0; i < width * height; i++) {
            int root = find(i);
            if (roots.contains(root)) {
                boxes.putIfAbsent(root, boundingBoxes[root]);
            }
        }

        return new HashSet<>(boxes.values());
    }

    // Helper class to represent a bounding box
    static class BoundingBox {
        int minX, minY, maxX, maxY;

        BoundingBox(int x, int y) {
            this.minX = x;
            this.minY = y;
            this.maxX = x;
            this.maxY = y;
        }

        // Update bounding box dimensions
        void update(BoundingBox other) {
            this.minX = Math.min(this.minX, other.minX);
            this.minY = Math.min(this.minY, other.minY);
            this.maxX = Math.max(this.maxX, other.maxX);
            this.maxY = Math.max(this.maxY, other.maxY);
        }

    }

    public Set<Point> searchColorBlackBackground(Image image, Color targetColor, double threshold) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        Set<Point> matchingPixels = new HashSet<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = pixelReader.getColor(x, y);

                if (pixelColor != null && colorDistance(pixelColor, targetColor) <= threshold) {
                    matchingPixels.add(new Point(x, y));
                }
            }
        }

        return matchingPixels;
    }
    public Set<Set<Point>> searchColorBlackBackgroundColored(Image image, Color targetColor, double threshold) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader pixelReader = image.getPixelReader();
        boolean[][] visited = new boolean[height][width];
        List<Set<Point>> regions = new ArrayList<>();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (visited[y][x]) continue;

                Color pixelColor = pixelReader.getColor(x, y);

                if (pixelColor != null && colorDistance(pixelColor, targetColor) <= threshold) {
                    Set<Point> region = new HashSet<>();
                    Queue<Point> queue = new LinkedList<>();
                    queue.add(new Point(x, y));
                    visited[y][x] = true;

                    while (!queue.isEmpty()) {
                        Point point = queue.poll();
                        region.add(point);

                        for (int[] dir : directions) {
                            int newY = point.y + dir[0];
                            int newX = point.x + dir[1];

                            if (newY >= 0 && newY < height && newX >= 0 && newX < width && !visited[newY][newX]) {
                                Color neighborColor = pixelReader.getColor(newX, newY);
                                if (neighborColor != null && colorDistance(neighborColor, targetColor) <= threshold) {
                                    visited[newY][newX] = true;
                                    queue.add(new Point(newX, newY));
                                }
                            }
                        }
                    }
                    regions.add(region);
                }
            }
        }
        return new HashSet<>(regions);
    }

}


