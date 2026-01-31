/**
 * Geographic helper utilities
 */
public final class GeoUtils {
    private GeoUtils() {}

    private static final double EARTH_RADIUS_MILES = 3958.7613;

    // Using haversine formula since it was the most credible result I found to calculate distance between two geo points
    public static double milesBetween(GeoPoint a, GeoPoint b) {
        if (a == null || b == null) return Double.NaN;

        double latitude1 = Math.toRadians(a.latitude);
        double longitude1 = Math.toRadians(a.longitude);
        double latitude2 = Math.toRadians(b.latitude);
        double longitude2 = Math.toRadians(b.longitude);

        double differenceLat = latitude2 - latitude1;
        double differenceLon = longitude2 - longitude1;

        double sinHalfLat = Math.sin(differenceLat / 2.0);
        double sinHalfLon = Math.sin(differenceLon / 2.0);

        double haversineVal = sinHalfLat * sinHalfLat + 
                            Math.cos(latitude1) * Math.cos(latitude2) *
                            sinHalfLon * sinHalfLon;

        double centralAngle = 2.0 * Math.atan2(Math.sqrt(haversineVal), Math.sqrt(1.0 - haversineVal));

        return EARTH_RADIUS_MILES * centralAngle;
    }
}