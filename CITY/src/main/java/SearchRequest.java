import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents one search query/user context
 */
public final class SearchRequest {
    public final Set<String> desiredTags;
    public final GeoPoint userLocation;
    public final Double maxDistanceMiles;
    public final Integer maxPriceLevel;
    public final boolean openNowOnly;
    public final LocalTime queryTime;
    
    public SearchRequest(
        Set<String> desiredTags,
        GeoPoint userLocation,
        Double maxDistanceMiles,
        Integer maxPriceLevel,
        boolean openNowOnly,
        LocalTime queryTime
    ) {
        this.desiredTags = normalizeTags(desiredTags);
        this.userLocation = userLocation;
        this.maxDistanceMiles = maxDistanceMiles;
        this.maxPriceLevel = maxPriceLevel;
        this.openNowOnly = openNowOnly;
        this.queryTime = (queryTime == null) ? LocalTime.now() : queryTime;
    }

    private static Set<String> normalizeTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) return Collections.emptySet();
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag == null) continue;
            String s = tag.trim().toLowerCase();
            if (!s.isEmpty()) out.add(s);
        }
        return Collections.unmodifiableSet(out);
    }

    @Override
    public String toString() {
        return "SearchRequest{" + "desiredTags=" + desiredTags + ", userLocation=" +
        userLocation + ", maxDistanceMiles=" + maxDistanceMiles + ", maxPriceLevel=" +
        maxPriceLevel + ", openNowOnly=" + openNowOnly + ", queryTime=" + queryTime +
        '}';
    }
}