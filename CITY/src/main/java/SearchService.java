import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// Core search logic
public final class SearchService {
    private final List<Poi> candidates;

    public SearchService(List<Poi> candidates) {
        this.candidates = (candidates == null) ? List.of() : List.copyOf(candidates);
    }

    public List<SearchResult> search(SearchRequest req) {
        if (req == null) return Collections.emptyList();

        final Double maxDist = req.maxDistanceMiles;
        final Integer maxPrice = req.maxPriceLevel;

        List<SearchResult> results = new ArrayList<>();

        for (Poi poi : candidates) {
            // Filter price
            if (maxPrice != null && maxPrice > 0) {
                if (poi.priceRange != null && !poi.priceRange.withinMax(maxPrice)) {
                    continue;
                }
            }

            // Filter open-now
            if (req.openNowOnly) {
                if (!isOpenNow(poi, req.queryTime)) continue;
            }
            
            // compute distance
            double dist = computeDistanceMiles(req.userLocation, poi.location);

            // Filter max distance
            if (maxDist != null && maxDist > 0) {
                if (!Double.isNaN(dist) && dist > maxDist) continue;
            }

            // Scoring
            int tagMatches = countTagMatches(poi.tags, req.desiredTags);

            double score = 0.0;
            List<String> why = new ArrayList<>();

            score += tagMatches * 100.0;
            if (tagMatches > 0) why.add("matches " + tagMatches + " tag(s)");

            if (isOpenNow(poi, req.queryTime)) {
                score += 15.0;
                why.add("open now");
            }

            if (!Double.isNaN(dist)) {
                why.add(String.format("%.2f mi away", dist));

                if (maxDist != null && maxDist > 0) {
                    double closeness = clamp01((maxDist - dist) / maxDist);
                    score += closeness * 40.0;
                } else {
                    score += 20.0 / (1.0 + dist);
                }
            } else {
                score -= 5.0;
                why.add("distance unknown");
            }

            // Price bonus
            if (maxPrice != null && maxPrice > 0 && poi.priceRange != null) {
                if (poi.priceRange.isUnknown()) {
                    why.add("price unknown");
                } else if (poi.priceRange.withinMax(maxPrice)) {
                    score += 5.0;
                    why.add("within budget");
                }
            }

            results.add(new SearchResult(poi, dist, tagMatches, score, why));
        }

        Collections.sort(results);
        return results;
    }

    private static double computeDistanceMiles(GeoPoint user, GeoPoint poi) {
        if (user == null || poi == null) return Double.NaN;
        return GeoUtils.milesBetween(user, poi);
    }

    private static boolean isOpenNow(Poi poi, LocalTime time) {
        if (poi.hours == null) return true;
        try {
            return poi.hours.isOpenAt(time);
        } catch (Exception e) {
            return true;
        }
    }

    private static int countTagMatches(Set<String> poiTags, Set<String> queryTags) {
        if (queryTags == null || queryTags.isEmpty()) return 0;
        if (poiTags == null || poiTags.isEmpty()) return 0;

        int count = 0;
        for (String tag : queryTags) {
            if (tag == null) continue;
            String norm = tag.trim().toLowerCase();
            if (norm.isEmpty()) continue;
            if (poiTags.contains(norm)) count++;
        }
        return count;
    }

    private static double clamp01(double x) {
        if (x < 0) return 0;
        if (x > 1) return 1;
        return x;
    }
}
