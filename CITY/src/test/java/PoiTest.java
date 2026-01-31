import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.LinkedHashSet;
import java.util.Set;

public class PoiTest {
    @Test
    void constructorNormalizesNullsAndTags() {
        Set<String> tags = new LinkedHashSet<>();
        tags.add(" Food ");
        tags.add("COFFEE");
        tags.add(null);
        tags.add(" ");

        Poi p = new Poi(null, null, tags, null, null, null, null, null);

        assertEquals("", p.id);
        assertEquals("", p.name);
        assertEquals(Set.of("food", "coffee"), p.tags);
        assertNotNull(p.priceRange);
        assertNotNull(p.hours);
        assertEquals("", p.address);
        assertNull(p.location);
        assertEquals("", p.description);
    }

    @Test
    void hasAnyTagTrueIfQueryEmpty() {
        Poi p = new Poi("1", "x", Set.of("food"), PriceRange.unknown(), HoursRange.unknown(), "", null, "");
        assertTrue(p.hasAnyTag(null));
        assertTrue(p.hasAnyTag(Set.of()));
    }

    @Test
    void hasAnyTagMatchesCaseAndWhitespace() {
        Poi p = new Poi("1", "x", Set.of("food", "coffee"), PriceRange.unknown(), HoursRange.unknown(), "", null, "");
        assertTrue(p.hasAnyTag(Set.of("  FOOD ")));
        assertTrue(p.hasAnyTag(Set.of("tea", "Coffee")));
        assertFalse(p.hasAnyTag(Set.of("tea", "bar")));
    }

    @Test
    void tagMatchCountCountsNormalizedMatches() {
        Poi p = new Poi("1", "x", Set.of("food", "coffee"), PriceRange.unknown(), HoursRange.unknown(), "", null, "");
        assertEquals(0, p.tagMatchCount(null));
        assertEquals(0, p.tagMatchCount(Set.of()));
        assertEquals(1, p.tagMatchCount(Set.of("FOOD")));
        assertEquals(2, p.tagMatchCount(Set.of("food", " coffee ")));
        assertEquals(1, p.tagMatchCount(Set.of("food", "nope")));
    }
}