import java.time.LocalTime;
import java.util.Objects;

public final class HoursRange {

    public final int openHour;

    public final int closeHour;

    private HoursRange(int openHour, int closeHour) {
        this.openHour = openHour;
        this.closeHour = closeHour;
    }

    public static HoursRange parse(String raw) {
        if (raw == null) return new HoursRange(0, 0);

        String s = raw.trim();
        String[] parts = s.split("-");
        if (parts.length != 2) return new HoursRange(0, 0);

        int open = safeParseHour(parts[0]);
        int close = safeParseHour(parts[1]);
        return new HoursRange(open, close);
    }

    private static int safeParseHour(String s) {
        try {
            int h = Integer.parseInt(s.trim());
            if (h < 0) return 0;
            if (h > 23) return h % 24;
            return h;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isUnknown() {
        return openHour == 0 && closeHour == 0;
    }

    public static HoursRange unknown() {
        return new HoursRange(0, 0);
    }

    public boolean is24Hours() {
        return openHour == closeHour;
    }

    public boolean isOpenAt(LocalTime time) {
        Objects.requireNonNull(time);

        if (is24Hours()) return true;

        int h = time.getHour();

        if (closeHour > openHour) {
            // Normal same-day range [open, close)
            return h >= openHour && h < closeHour;
        }

        // Overnight range: open from openHour -> 23, and 0 -> closeHour
        return h >= openHour || h < closeHour;
    }

    @Override
    public String toString() {
        if (is24Hours()) return "24h";
        return openHour + "-" + closeHour;
    }
}
