import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class SearchRequestTest {
    @Test
    void normalizesTagsAndMakesUnmodifiable() {
        Set<String> raw = new LinkedHashSet<>();
        raw.add(" Food ");
        raw.add("COFFEE");
        raw.add(null);
        raw.add("  ");

        SearchRequest req = new SearchRequest(raw, null, null, null, false, null);
        
        assertEquals(Set.of("food", "coffee"), req.desiredTags);
        assertThrows(UnsupportedOperationException.class, () -> req.desiredTags.add("newtag"));
    }

    @Test
    void nullOrEmptyTagsBecomeEmptySet() {
        SearchRequest req1 = new SearchRequest(null, null, null, null, false, LocalTime.of(10, 0));
        assertNotNull(req1.desiredTags);
        assertTrue(req1.desiredTags.isEmpty());

        SearchRequest req2 = new SearchRequest(Set.of(), null, null, null, false, LocalTime.of(10, 0));
        assertNotNull(req2.desiredTags);
        assertTrue(req2.desiredTags.isEmpty());
    }

    @Test
    void nullQueryTimeDefaultsToNow() {
        SearchRequest req = new SearchRequest(Set.of("food"), null, null, null, false, null);
        assertNotNull(req.queryTime);
    }
}
