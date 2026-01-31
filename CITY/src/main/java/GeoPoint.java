import java.util.Objects;

// Immutable value object for geographic coordinates
public final class GeoPoint {
    // values of latitude and longitude are in degrees
    public final double latitude;
    public final double longitude;

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "GeoPoint{" + "latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    // Equality based on coordinate values
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoPoint gp)) return false;
        return Double.compare(latitude, gp.latitude) == 0 && Double.compare(longitude, gp.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}