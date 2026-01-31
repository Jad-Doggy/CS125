import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GeoUtilsTest {
    @Test
    void milesBetweenReturnsNaNOnNulls() {
        assertTrue(Double.isNaN(GeoUtils.milesBetween(null, null)));
        assertTrue(Double.isNaN(GeoUtils.milesBetween(new GeoPoint(0, 0), null)));
    }

    @Test
    void milesBetweenZeroForSamePoint() {
        GeoPoint p = new GeoPoint(10.0, 20.0);
        assertEquals(0.0, GeoUtils.milesBetween(p, p), 1e-9);
    }

    @Test
    void milesBetweenIsReasonableForKnownCities() {
        // Los Angeles (approx) to New York City (approx) is ~2445 miles.
        GeoPoint la = new GeoPoint(34.0522, -118.2437);
        GeoPoint nyc = new GeoPoint(40.7128, -74.0060);
        double miles = GeoUtils.milesBetween(la, nyc);

        assertFalse(Double.isNaN(miles));
        assertTrue(miles > 2200 && miles < 2700, "distance was " + miles);
    }
}
