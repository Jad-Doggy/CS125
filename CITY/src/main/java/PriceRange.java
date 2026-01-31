import java.util.Objects;

/**
 * Represents a price range using a dollar-sign scale
 * E.g: $ -> 1, $$ -> 2, $$$ -> 3, etc.
 * Unknown price is represented as (0, 0)
 */
public final class PriceRange {
    public final int minPriceLevel;
    public final int maxPriceLevel;

    private PriceRange(int minPriceLevel, int maxPriceLevel) {
        this.minPriceLevel = clamp(minPriceLevel);
        this.maxPriceLevel = clamp(maxPriceLevel);
    }

    public static PriceRange of(int minPriceLevel, int maxPriceLevel) {
        return new PriceRange(minPriceLevel, maxPriceLevel);
    }

    public static PriceRange exact(int level) {
        return new PriceRange(level, level);
    }

    public static PriceRange unknown() {
        return new PriceRange(0, 0);
    }

    public boolean isUnknown() {
        return minPriceLevel == 0 && maxPriceLevel == 0;
    }

    // Checks whether this POI is compatible with a user's max price preference
    public boolean withinMax(int maxAllowed) {
        if (maxAllowed <= 0) return true;
        if (isUnknown()) return true;
        return minPriceLevel <= maxAllowed || maxPriceLevel <= maxAllowed;
    }

    public static PriceRange parse(String raw) {
        if (raw == null) return unknown();
        String s = raw.trim();
        if (s.isEmpty()) return unknown();

        // Dollar-based input
        if (s.contains("$")) {
            String normalized = s.replaceAll("\\p{Pd}-", "-"); // just making sure in case format use em or en dash
            String[] parts = normalized.split("-");
            if (parts.length == 1) {
                int lvl = countDollars(parts[0]);
                return lvl == 0 ? unknown() : exact(lvl);
            } else {
                int a = countDollars(parts[0]);
                int b = countDollars(parts[1]);
                if (a == 0 && b == 0) return unknown();
                if (a == 0) a = b;
                if (b == 0) b = a;
                return of(Math.min(a, b), Math.max(a, b));
            }
        }

        // Numeric input
        try {
            String normalized = s.replaceAll("\\p{Pd}-", "-");
            String[] parts = normalized.split("-");
            if (parts.length == 1) {
                int lvl = Integer.parseInt(parts[0]);
                return lvl <= 0 ? unknown() : exact(lvl);
            } else {
                int a = Integer.parseInt(parts[0]);
                int b = Integer.parseInt(parts[1]);
                if (a <= 0 && b <= 0) return unknown();
                if (a <= 0) a = b;
                if (b <= 0) b = a;
                return of(Math.min(a, b), Math.max(a, b));
            }
        } catch (NumberFormatException e) {
            return unknown();
        }
    }

    private static int countDollars(String part) {
        int count = 0;
        for (char ch : part.toCharArray()) {
            if (ch == '$') count++;
        }
        return count;
    }

    private static int clamp(int lvl) {
        if (lvl <= 0) return 0;
        if (lvl > 4) return 4;
        return lvl;
    }

    // Format for API output
    public String format() {
        if (isUnknown()) return "?";
        if (minPriceLevel == maxPriceLevel) return "$".repeat(minPriceLevel);
        return "$".repeat(minPriceLevel) + "-" + "$".repeat(maxPriceLevel);
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceRange pr)) return false;
        return minPriceLevel == pr.minPriceLevel && maxPriceLevel == pr.maxPriceLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minPriceLevel, maxPriceLevel);
    }
}