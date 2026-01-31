import java.util.List;

class Main {
    public static void main(String[] args) {
        ReadCSV readCsv = new ReadCSV("pois.csv");
        List<Poi> poiList = readCsv.returnPOIs();
        printPois(poiList);
    }

    /**
     * Debug printer so you can verify the CSV -> Poi mapping.
     * Prints fields that now exist in Poi:
     *  - id, name, tags, priceRange, hours, address, location, description
     */
    static void printPois(List<Poi> list) {
        int i = 1;

        for (Poi poi : list) {
            String coordStr;
            if (poi.location == null) {
                coordStr = "unknown";
            } else {
                coordStr = String.format("(%.6f, %.6f)", poi.location.latitude, poi.location.longitude);
            }

            // PriceRange and HoursRange should have useful toString() implementations.
            // If not, swap these to print raw fields.
            String priceStr = (poi.priceRange == null) ? "unknown" : poi.priceRange.toString();
            String hoursStr = (poi.hours == null) ? "unknown" : poi.hours.toString();

            System.out.printf(
                    "Location %d [%s]: %s%n" +
                    "Tags: %s%n" +
                    "Price: %s%n" +
                    "Hours: %s%n" +
                    "Address: %s%n" +
                    "Coordinates: %s%n" +
                    "Description: %s%n%n",
                    i,
                    poi.id,
                    poi.name,
                    poi.tags,
                    priceStr,
                    hoursStr,
                    poi.address,
                    coordStr,
                    poi.description
            );

            i++;
        }
    }
}
