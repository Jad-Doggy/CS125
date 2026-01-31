import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PriceRangeTest {
    @Test
    void parseDollarExact() {
        assertEquals(PriceRange.exact(2), PriceRange.parse("$$"));
    }

    @Test
    void parseDollarRange() {
        assertEquals(PriceRange.of(1, 3), PriceRange.parse("$-$$$"));
    }

    @Test
    void parseNumericRangeOrdersMinMax() {
        assertEquals(PriceRange.of(1, 3), PriceRange.parse("3-1"));
    }

    @Test
    void parseInvalidGivesUnknown() {
        assertEquals(PriceRange.unknown(), PriceRange.parse("not-a-price"));
        assertEquals(PriceRange.unknown(), PriceRange.parse(""));
        assertEquals(PriceRange.unknown(), PriceRange.parse(null));
    }

    @Test
    void withinMaxBehavior() {
        assertTrue(PriceRange.unknown().withinMax(1));
        assertTrue(PriceRange.of(3, 4).withinMax(3));
        assertFalse(PriceRange.of(3, 4).withinMax(2));
        assertTrue(PriceRange.of(4, 4).withinMax(0));
    }

    @Test
    void formatWorks() {
        assertEquals("?", PriceRange.unknown().format());
        assertEquals("$$", PriceRange.exact(2).format());
        assertEquals("$-$$$", PriceRange.of(1, 3).format());
    }
}