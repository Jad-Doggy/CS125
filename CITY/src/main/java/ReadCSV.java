import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class ReadCSV
{
    private String fileName = null;
    private final List<Poi> pois = new ArrayList<>();

    public ReadCSV(String FileName)
    {
        fileName = FileName;
        Read();
    }
    
    void Read()
    {
        if (fileName == null) return;

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null) throw new RuntimeException("CSV not found: " + fileName);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int row = 0;

            while ((line = bufferedReader.readLine()) != null) {
                row++;

                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                String[] values = trimmed.split(",");
                if (values.length < 8) {
                    System.err.println("Skipped malformed row " + row + " (expected 8 columns, got " + values.length + ")");
                    continue;
                }

                // columns
                String name = values[0].trim();
                String type1 = values[1].trim();
                String type2 = values[2].trim();
                String priceRaw = values[3].trim();
                String hoursRaw = values[4].trim();
                String address = values[5].trim().replace('|', ',');
                String coordRaw = values[6].trim();

                // Build tags set from type1/type2
                Set<String> tags = new LinkedHashSet<>();
                addTag(tags, type1);
                addTag(tags, type2);

                PriceRange priceRange = PriceRange.parse(priceRaw);
                HoursRange hours = HoursRange.parse(hoursRaw);
                GeoPoint location = parseGeoPoint(coordRaw);

                String description = "";
                String id = "poi-" + row;
                Poi poi = new Poi(id, name, tags, priceRange, hours, address, location, description);
                pois.add(poi);
            }
        } catch (IOException e) {
            System.err.println("An error occurred reading CSV: " + e.getMessage());
        }
    }

    private static void addTag(Set<String> tags, String raw) {
        if (raw == null) return;
        String trimmed = raw.trim().toLowerCase();
        if (trimmed.isEmpty() || trimmed.equals("none")) return;
        tags.add(trimmed);
    }

    private static GeoPoint parseGeoPoint(String raw) {
        if (raw == null) return null;

        String s = raw.trim();
        if (s.isEmpty() || s.equals("0")) return null;

        // expected "lat|lon"
        String[] parts = s.split("\\|");
        if (parts.length != 2) return null;

        try {
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());
            return new GeoPoint(latitude, longitude);
        } catch (Exception e) {
            return null;
        }
    }

    List<Poi> returnPOIs() {
        return pois;
    }
}
