import os
import time
import requests
import csv
from dotenv import load_dotenv
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
INPUT_CSV = BASE_DIR / "resources" / "pois.csv"
OUTPUT_CSV = BASE_DIR / "resources" / "pois_geocoded.csv"
LOCATIONIQ_BASE = "https://us1.locationiq.com/v1/search"  # US endpoint
RATE_LIMIT_DELAY = 1.0

load_dotenv()
API_KEY = os.getenv("LOCATIONIQ_API_KEY")
if not API_KEY:
    raise RuntimeError("LOCATIONIQ_API_KEY not found in environment (.env).")

# Strip quotes
API_KEY = API_KEY.strip()
if API_KEY.startswith('"') and API_KEY.endswith('"'):
    API_KEY = API_KEY[1:-1]

def parse_geo_point(raw: str):
    if raw is None:
        return None
    s = str(raw).strip()
    if s == "" or s == "0":
        return None
    parts = s.split("|")
    if len(parts) != 2:
        return None
    try:
        lat = float(parts[0].strip())
        lon = float(parts[1].strip())
        return lat, lon
    except Exception:
        return None

#get lat/lon (none/none on failure)
def geocode_address(address: str, session: requests.Session, countrycodes: str = None):
    params = {
        "key": API_KEY,
        "q": address,
        "format": "json",
        "limit": 1
    }
    if countrycodes:
        params["countrycodes"] = countrycodes
    try:
        resp = session.get(LOCATIONIQ_BASE, params=params, timeout=10)
        resp.raise_for_status()
        j = resp.json()
        if isinstance(j, list) and len(j) > 0:
            first = j[0]
            lat = float(first.get("lat"))
            lon = float(first.get("lon"))
            return lat, lon
    except Exception as e:
        # don't raise here; caller will log and continue
        print(f"  [geocode error] {e}")
    return None, None

def main():
    print("Reading CSV from:", INPUT_CSV.resolve())

    if not INPUT_CSV.exists():
        raise RuntimeError(f"Input CSV not found: {INPUT_CSV}")

    total = 0
    skipped = 0
    updated = 0
    failed = 0

    rows = []

    session = requests.Session()

    with open(INPUT_CSV, newline='', encoding='utf-8') as f:
        reader = csv.reader(f)
        for row in reader:
            total += 1

            # Ensure at least 7 columns
            while len(row) < 7:
                row.append("")

            raw_coord = row[6]
            parsed = parse_geo_point(raw_coord)

            if parsed is not None:
                skipped += 1
                rows.append(row)
                continue

            raw_address = row[5]
            query_address = raw_address.replace("|", ",").strip()

            if not query_address:
                print(f"[{total}] Empty address, skipping.")
                failed += 1
                rows.append(row)
                continue

            print(f"[{total}] Geocoding: {query_address}")
            lat, lon = geocode_address(query_address, session)

            if lat is not None and lon is not None:
                row[6] = f"{lat}|{lon}"
                print(f"  -> {row[6]}")
                updated += 1
            else:
                print("  -> failed")
                failed += 1

            rows.append(row)
            time.sleep(RATE_LIMIT_DELAY)

    # Write output
    with open(OUTPUT_CSV, "w", newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerows(rows)

    print("---- Summary ----")
    print(f"Total rows: {total}")
    print(f"Skipped: {skipped}")
    print(f"Updated: {updated}")
    print(f"Failed: {failed}")
    print(f"Output written to: {OUTPUT_CSV}")

if __name__ == "__main__":
    main()