import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SearchResultTest {

    private static Poi poiNamed(String name) {
        return new Poi(
                "id-" + name,
                name,
                Set.of("food"),
                PriceRange.unknown(),
                HoursRange.unknown(),
                "",
                null,
                ""
        );
    }

    @Test
    void compareToSortsByScoreDescThenDistanceAscThenName() {
        Poi a = poiNamed("Alpha");
        Poi b = poiNamed("Beta");
        Poi c = poiNamed("Charlie");

        // Higher score should come first
        SearchResult r1 = new SearchResult(a, 2.0, 1, 200.0, List.of());
        SearchResult r2 = new SearchResult(b, 1.0, 1, 300.0, List.of());
        SearchResult r3 = new SearchResult(c, 0.5, 1, 100.0, List.of());

        List<SearchResult> list = new ArrayList<>(List.of(r1, r2, r3));
        Collections.sort(list);

        assertSame(b, list.get(0).poi); // score 300
        assertSame(a, list.get(1).poi); // score 200
        assertSame(c, list.get(2).poi); // score 100
    }

    @Test
    void compareToUsesDistanceWhenScoresTie() {
        Poi near = poiNamed("Near");
        Poi far = poiNamed("Far");

        SearchResult rNear = new SearchResult(near, 1.0, 1, 200.0, List.of());
        SearchResult rFar  = new SearchResult(far,  5.0, 1, 200.0, List.of());

        List<SearchResult> list = new ArrayList<>(List.of(rFar, rNear));
        Collections.sort(list);

        assertSame(near, list.get(0).poi); // same score, nearer distance
        assertSame(far, list.get(1).poi);
    }

    @Test
    void compareToPrefersKnownDistanceOverNaNWhenScoresTie() {
        Poi known = poiNamed("Known");
        Poi unknown = poiNamed("Unknown");

        SearchResult rKnown = new SearchResult(known, 2.0, 1, 200.0, List.of());
        SearchResult rUnknown = new SearchResult(unknown, Double.NaN, 1, 200.0, List.of());

        List<SearchResult> list = new ArrayList<>(List.of(rUnknown, rKnown));
        Collections.sort(list);

        assertSame(known, list.get(0).poi);
        assertSame(unknown, list.get(1).poi);
    }
}
