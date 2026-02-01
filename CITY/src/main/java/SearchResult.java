import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * One ranked result returned by SearchService
 */
public final class SearchResult implements Comparable<SearchResult> {
    public final Poi poi;
    public final double distanceMiles;
    public final int tagMatches; // number of matched tags from request.desiredTags
    public final double score;
    public final List<String> why; // small explanation list for UI ("matches food + coffee", "1.2 mi away", "open now")

    public SearchResult(Poi poi, double distanceMiles, int tagMatches, double score, List<String> why) {
        this.poi = Objects.requireNonNull(poi);
        this.distanceMiles = distanceMiles;
        this.tagMatches = tagMatches;
        this.score = score;

        if (why == null) this.why = Collections.emptyList();
        else this.why = Collections.unmodifiableList(new ArrayList<>(why));
    }

    @Override
    public int compareTo(SearchResult other) {
        int byScore = Double.compare(other.score, this.score);
        if (byScore != 0) return byScore;

        boolean thisKnown = !Double.isNaN(this.distanceMiles);
        boolean otherKnown = !Double.isNaN(other.distanceMiles);
        if (thisKnown && otherKnown) {
            int byDist = Double.compare(this.distanceMiles, other.distanceMiles);
            if (byDist != 0) return byDist;
        } else if (thisKnown != otherKnown) {
            // Prefer known distance over unknown distance
            return thisKnown ? -1 : 1;
        }
        
        return this.poi.name.compareToIgnoreCase(other.poi.name);
    }
}
