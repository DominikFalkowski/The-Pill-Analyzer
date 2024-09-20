package org.example.pillanalyzer20102969;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;


public class Pill {
    private Color color;
    private String name;
    private String description;

    public Pill(Color color, String name, String description) {
        this.color = color;
        this.name = name;
        this.description = description;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}



