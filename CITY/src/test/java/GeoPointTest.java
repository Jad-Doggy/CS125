import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class GeoPointTest {
    @Test
    void equalityAndHashCodeMatch() {
        GeoPoint a = new GeoPoint(34.0, -118.0);
        GeoPoint b = new GeoPoint(34.0, -118.0);
        GeoPoint c = new GeoPoint(40.0, -118.0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}