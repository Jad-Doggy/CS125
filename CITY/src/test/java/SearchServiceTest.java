import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class SearchServiceTest {

    private static Poi poi(
            String id,
            String name,
            Set<String> tags,
            PriceRange price,
            HoursRange hours,
            GeoPoint location
    ) {
        return new Poi(id, name, tags, price, hours, "addr", location, "desc");
    }

    @Test
    void filtersOutPoisWithNoTagMatchWhenDesiredTagsProvided() {
        Poi coffee = poi("1", "Coffee Spot", Set.of("coffee", "food"),
                PriceRange.exact(2), HoursRange.parse("8-20"), new GeoPoint(0.01, 0.0));
        Poi park = poi("2", "Park", Set.of("outdoors"),
                PriceRange.unknown(), HoursRange.parse("8-20"), new GeoPoint(0.02, 0.0));

        SearchService svc = new SearchService(List.of(coffee, park));

        SearchRequest req = new SearchRequest(
                Set.of("coffee"),
                new GeoPoint(0.0, 0.0),
                null,
                null,
                false,
                LocalTime.of(10, 0)
        );

        List<SearchResult> results = svc.search(req);
        assertEquals(2, results.size());
        assertEquals("Coffee Spot", results.get(0).poi.name);
    }

    @Test
    void priceFilterExcludesOutOfBudgetWhenMaxPriceProvided() {
        Poi cheap = poi("1", "Cheap", Set.of("food"),
                PriceRange.exact(1), HoursRange.parse("8-20"), null);
        Poi pricey = poi("2", "Pricey", Set.of("food"),
                PriceRange.exact(3), HoursRange.parse("8-20"), null);

        SearchService svc = new SearchService(List.of(cheap, pricey));

        SearchRequest req = new SearchRequest(
                Set.of("food"),
                null,
                null,
                1,      // max $
                false,
                LocalTime.of(10, 0)
        );

        List<SearchResult> results = svc.search(req);
        assertEquals(1, results.size());
        assertEquals("Cheap", results.get(0).poi.name);
    }

    @Test
    void openNowOnlyFiltersOutClosedPois() {
        Poi open = poi("1", "Open", Set.of("food"),
                PriceRange.exact(2), HoursRange.parse("8-20"), null);
        Poi closed = poi("2", "Closed", Set.of("food"),
                PriceRange.exact(2), HoursRange.parse("8-9"), null);

        SearchService svc = new SearchService(List.of(open, closed));

        SearchRequest req = new SearchRequest(
                Set.of("food"),
                null,
                null,
                null,
                true,
                LocalTime.of(10, 0)
        );

        List<SearchResult> results = svc.search(req);
        assertEquals(1, results.size());
        assertEquals("Open", results.get(0).poi.name);
    }

    @Test
    void maxDistanceFiltersKnownFarPoisButKeepsUnknownDistancePois() {
        GeoPoint user = new GeoPoint(0.0, 0.0);

        Poi near = poi("1", "Near", Set.of("food"),
                PriceRange.exact(2), HoursRange.parse("8-20"), new GeoPoint(0.01, 0.0));

        Poi far = poi("2", "Far", Set.of("food"),
                PriceRange.exact(2), HoursRange.parse("8-20"), new GeoPoint(0.5, 0.0));

        Poi unknown = poi("3", "UnknownDist", Set.of("food"),
                PriceRange.exact(2), HoursRange.parse("8-20"), null);

        SearchService svc = new SearchService(List.of(near, far, unknown));

        SearchRequest req = new SearchRequest(
                Set.of("food"),
                user,
                5.0,    // 5 miles max
                null,
                false,
                LocalTime.of(10, 0)
        );

        List<SearchResult> results = svc.search(req);

        assertTrue(results.stream().noneMatch(r -> r.poi.name.equals("Far")));

        assertTrue(results.stream().anyMatch(r -> r.poi.name.equals("Near")));

        assertTrue(results.stream().anyMatch(r -> r.poi.name.equals("UnknownDist")));
    }

    @Test
    void rankingPrefersMoreTagMatchesOverCloserDistance() {
        GeoPoint user = new GeoPoint(0.0, 0.0);

        // 1 tag match, very close
        Poi closeOneTag = poi("1", "CloseOneTag", Set.of("coffee"),
                PriceRange.exact(2), HoursRange.parse("8-20"), new GeoPoint(0.01, 0.0));

        // 2 tag matches, farther
        Poi farTwoTags = poi("2", "FarTwoTags", Set.of("coffee", "food"),
                PriceRange.exact(2), HoursRange.parse("8-20"), new GeoPoint(0.2, 0.0));

        SearchService svc = new SearchService(List.of(closeOneTag, farTwoTags));

        SearchRequest req = new SearchRequest(
                Set.of("coffee", "food"),
                user,
                50.0,
                2,
                false,
                LocalTime.of(10, 0)
        );

        List<SearchResult> results = svc.search(req);

        assertEquals("FarTwoTags", results.get(0).poi.name);
        assertEquals("CloseOneTag", results.get(1).poi.name);
    }

    @Test
    void rankingPrefersKnownWithinBudgetOverUnknownPriceWhenOtherFactorsTie() {
        Poi knownPrice = poi("1", "KnownPrice", Set.of("food"),
                PriceRange.exact(2), HoursRange.parse("8-20"), null);

        Poi unknownPrice = poi("2", "UnknownPrice", Set.of("food"),
                PriceRange.unknown(), HoursRange.parse("8-20"), null);

        SearchService svc = new SearchService(List.of(unknownPrice, knownPrice));

        SearchRequest req = new SearchRequest(
                Set.of("food"),
                null,
                null,
                2,      // within budget for knownPrice
                false,
                LocalTime.of(10, 0)
        );

        List<SearchResult> results = svc.search(req);

        // knownPrice gets the +5 "within budget" bonus, unknownPrice does not
        assertEquals("KnownPrice", results.get(0).poi.name);
        assertEquals("UnknownPrice", results.get(1).poi.name);
    }
}
