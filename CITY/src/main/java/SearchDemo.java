import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public final class SearchDemo {

    private static void printResults(List<SearchResult> results) {

        System.out.println("\n===== Ranked Results =====\n");

        int rank = 1;

        for (SearchResult r : results) {

            System.out.println("Rank #" + rank++);
            System.out.println("Name: " + r.poi.name);
            System.out.println("Score: " + String.format("%.2f", r.score));

            if (!Double.isNaN(r.distanceMiles)) {
                System.out.println("Distance: " + String.format("%.2f miles", r.distanceMiles));
            }

            System.out.println("Why recommended:");
            for (String reason : r.why) {
                System.out.println("  • " + reason);
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        // Load POIs from your CSV
        ReadCSV reader = ReadCSV.fromResource("pois.csv");
        List<Poi> pois = reader.returnPOIs();
        System.out.println("Loaded " + pois.size() + " POIs");

        // Create search service
        SearchService service = new SearchService(pois);

        // Example user search request
        SearchRequest req = new SearchRequest(
                Set.of("coffee", "study"),
                new GeoPoint(34.0689, -118.4452), // UCLA
                10.0,
                3,
                false,
                LocalTime.now()
        );

        // Get top 5 ranked results
        List<SearchResult> top = service.searchTopK(req, 5);

        // Print results
        printResults(top);
    }
}
