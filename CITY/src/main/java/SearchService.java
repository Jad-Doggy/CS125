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
        final Set<String> query = req.desiredTags;

        List<SearchResult> results = new ArrayList<>();

        for (Poi poi : candidates) {
            // Filter price
            if (maxPrice != null && maxPrice > 0) {
                if (poi.priceRange != null && !poi.priceRange.withinMax(maxPrice)) {
                    continue;
                }
            }

            // Filter open-now ONLY when requested
            if (req.openNowOnly) {
                if (!isOpenNow(poi, req.queryTime)) continue;
            }

            // compute distance
            double dist = computeDistanceMiles(req.userLocation, poi.location);

            // Filter max distance
            if (maxDist != null && maxDist > 0) {
                if (!Double.isNaN(dist) && dist > maxDist) continue;
            }

            // --- Keyword matching (tags + name text) ---
            int tagMatches = countTagMatches(poi.tags, query);
            int nameMatches = countNameMatches(poi.name, query);
            int totalMatches = tagMatches + nameMatches;

            // IMPORTANT: If user typed keywords, require at least one to meet similarity threshold
            if (query != null && !query.isEmpty() && totalMatches == 0) {
                continue;
            }

            // Scoring
            double score = 0.0;
            List<String> why = new ArrayList<>();

            // Keyword score dominates
            score += totalMatches * 100.0;
            if (totalMatches > 0) {
                why.add("matches " + totalMatches + " keyword(s)");
            }

            // Only mention open-now if the user asked for it
            if (req.openNowOnly) {
                score += 15.0;
                why.add("open now");
            }

            // Distance score
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

            // Price bonus (only if user set a max price)
            if (maxPrice != null && maxPrice > 0 && poi.priceRange != null) {
                if (poi.priceRange.isUnknown()) {
                    why.add("price unknown");
                } else if (poi.priceRange.withinMax(maxPrice)) {
                    score += 5.0;
                    why.add("within budget");
                }
            }

            results.add(new SearchResult(poi, dist, totalMatches, score, why));
        }

        Collections.sort(results);
        return results;
    }

    public List<SearchResult> searchTopK(SearchRequest req, int k) {
        if (k <= 0) return Collections.emptyList();

        List<SearchResult> ranked = search(req);
        if (ranked.isEmpty()) return ranked;

        int end = Math.min(k, ranked.size());
        return List.copyOf(ranked.subList(0, end));
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

    // Calculates levenshtein distance for word similarity

    private static int countNameMatches(String name, Set<String> queryTags) {
        if (queryTags == null || queryTags.isEmpty()) return 0;
        if (name == null || name.isBlank()) return 0;

        String[] words = name.toLowerCase().split("\\s+");
        int count = 0;
        double threshold = 0.7;

        for (String q : queryTags) {
            if (q == null || q.isBlank()) continue;
            String query = q.toLowerCase();

            for (String word : words) {
                if (similarity(word, query) >= threshold) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }

    private static double similarity(String a, String b) {
        int dist = levenshtein(a, b);
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) dist / maxLen);
    }

    private static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length()+1][b.length()+1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i-1) == b.charAt(j-1) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1),
                        dp[i-1][j-1] + cost
                );
            }
        }

        return dp[a.length()][b.length()];
    }

    private static double clamp01(double x) {
        if (x < 0) return 0;
        if (x > 1) return 1;
        return x;
    }
}