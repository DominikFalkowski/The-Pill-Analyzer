package org.example.pillanalyzer20102969;

import javafx.scene.paint.Color;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class PillIdentifier {
    private static final Map<Color, Pill> pillMap = new HashMap<>();

    static {
        pillMap.put(Color.RED, new Pill(Color.RED, "Paracetamol", "Used for pain relief and fever reduction."));
        pillMap.put(Color.BLUE, new Pill(Color.BLUE, "Ibuprofen", "Used for treating pain, fever, and inflammation."));
        pillMap.put(Color.PURPLE, new Pill(Color.PURPLE, "Ms Contin", "Used to help relieve severe ongoing pain."));
        pillMap.put(Color.YELLOW, new Pill(Color.YELLOW, "Hydrocodone", "Used to relieve moderate to severe pain."));
        pillMap.put(Color.WHITE, new Pill(Color.WHITE, "Vicodin", "Used to relieve moderate to severe pain."));

    }

    public static Pill identifyPill(Color color) {
        return pillMap.getOrDefault(color, new Pill(color, "Unknown", "No information available"));
    }
}
