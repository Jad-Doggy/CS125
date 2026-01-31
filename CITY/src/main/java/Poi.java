import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Poi
{
    public final String id;
    public final String name;
    public final Set<String> tags; // normalized lowercase tags
    public final PriceRange priceRange;
    public final HoursRange hours; // assumes a single daily time range for MVP
    public final String address;
    public final GeoPoint location; // may be null/unknown
    public final String description;

    public Poi(String id,
            String name,
            Set<String> tags,
            PriceRange priceRange,
            HoursRange hours,
            String address,
            GeoPoint location,
            String description
    ) {
        this.id = Objects.requireNonNullElse(id, "");
        this.name = Objects.requireNonNullElse(name, "");
        this.tags = normalizeTags(tags);
        this.priceRange = priceRange == null ? PriceRange.unknown() : priceRange;
        this.hours = hours == null ? HoursRange.unknown() : hours;
        this.address = Objects.requireNonNullElse(address, "");
        this.location = location; // allowed to be null
        this.description = Objects.requireNonNullElse(description, "");
    }

    // Trim whitespace, lowercase, and remove null values
    private static Set<String> normalizeTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) return Set.of();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag == null) continue;
            String s = tag.trim().toLowerCase();
            if (!s.isEmpty()) out.add(s);
        }
        return Collections.unmodifiableSet(out);
    }

    // Returns true if this POI matches any of the query tags
    public boolean hasAnyTag(Set<String> queryTags) {
        if (queryTags == null || queryTags.isEmpty()) return true;

        for (String tag : queryTags) {
            if (tag == null) continue;
            if (tags.contains(tag.trim().toLowerCase())) return true;
        }
        return false;
    }

    // Counts how many query tags match this POI
    public int tagMatchCount(Set<String> queryTags) {
        if (queryTags == null || queryTags.isEmpty()) return 0;

        int count = 0;
        for (String tag : queryTags) {
            if (tag == null) continue;
            if (tags.contains(tag.trim().toLowerCase())) count++;
        }
        return count;
    }

    // You can adjust the format but at least to me this looks the most readable for debugging
    @Override
    public String toString() {
        return "Poi{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", tags=" + tags
        + ", priceRange=" + priceRange + ", hours=" + hours + ", address='" + address +'\''
        + ", location=" + location + '}';
    }
}