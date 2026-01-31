import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ReadCSVTest {

    @Test
    void readsValidRowsAndSkipsMalformedRows() {
        ReadCSV reader = new ReadCSV("test_pois.csv");
        List<Poi> pois = reader.returnPOIs();

        // test_pois.csv includes 2 valid rows, 1 malformed row, and 1 blank row.
        assertEquals(2, pois.size());

        Poi first = pois.get(0);
        assertEquals("poi-1", first.id);
        assertEquals("Cafe Alpha", first.name);
        assertTrue(first.tags.contains("food"));
        assertTrue(first.tags.contains("coffee"));
        assertEquals("123 Main St, Suite 2", first.address);
        assertNotNull(first.location);
        assertEquals(34.0, first.location.latitude, 1e-9);
        assertEquals(-118.0, first.location.longitude, 1e-9);

        Poi second = pois.get(1);
        assertEquals("poi-2", second.id);
        assertEquals("Budget Bites", second.name);
        // type2 is "none" in the CSV, should not be added
        assertEquals(1, second.tags.size());
        assertTrue(second.tags.contains("food"));
        // coordRaw is "0" in the CSV, should become null
        assertNull(second.location);
    }

    @Test
    void missingResourceThrows() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> new ReadCSV("does_not_exist.csv"));
        assertTrue(ex.getMessage().toLowerCase().contains("csv"));
    }
}