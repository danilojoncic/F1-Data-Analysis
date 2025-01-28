package big_data.january.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class GeoJsonExtractor {

    public static Shape extractFromFile(String filename) {
        Path2D track = new Path2D.Double();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(filename));

            JsonNode features = root.get("features");
            if (features == null || !features.isArray()) {
                throw new IllegalArgumentException("Invalid GeoJSON: No 'features' found.");
            }

            for (JsonNode feature : features) {
                JsonNode geometry = feature.get("geometry");
                if (geometry == null) {
                    continue;
                }

                String type = geometry.get("type").asText();
                JsonNode coordinates = geometry.get("coordinates");

                // Handle LineString geometry
                if ("LineString".equals(type)) {
                    track.moveTo(
                            coordinates.get(0).get(0).asDouble(),
                            coordinates.get(0).get(1).asDouble()
                    );

                    for (int i = 1; i < coordinates.size(); i++) {
                        double x = coordinates.get(i).get(0).asDouble();
                        double y = coordinates.get(i).get(1).asDouble();
                        track.lineTo(x, y);
                    }
                }
                else if ("MultiLineString".equals(type)) {
                    for (JsonNode line : coordinates) {
                        track.moveTo(
                                line.get(0).get(0).asDouble(),
                                line.get(0).get(1).asDouble()
                        );
                        for (int i = 1; i < line.size(); i++) {
                            double x = line.get(i).get(0).asDouble();
                            double y = line.get(i).get(1).asDouble();
                            track.lineTo(x, y);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading GeoJSON file: " + filename, e);
        }

        return track;
    }
}
