import { useEffect, useMemo, useState } from "react";
import FilterCard from "../components/FilterCard";
import PoiCard from "../components/PoiCard";

type SearchApiItem = {
  name: string;
  distanceMiles: number | null;
  tagMatches: number;
  score: number;
  why: string[];
};

type SearchApiResponse = { results: SearchApiItem[] };

function toTagsCsv(raw: string): string {
  return raw
    .split(/[,\s;]+/)
    .map((s) => s.trim())
    .filter(Boolean)
    .join(",");
}

export default function Search() {
  const [backendStatus, setBackendStatus] = useState<"loading" | "ok" | "error">(
    "loading"
  );

  const [query, setQuery] = useState("coffee");
  const [openNow, setOpenNow] = useState(false);
  const [maxDist, setMaxDist] = useState(""); // miles (optional)
  const [maxPrice, setMaxPrice] = useState(""); // int (optional)
  const [k, setK] = useState(25);

  const [showDetails, setShowDetails] = useState(true);

  const [results, setResults] = useState<SearchApiItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);

  // Location state
  const [lat, setLat] = useState<number | null>(null);
  const [lon, setLon] = useState<number | null>(null);
  const [locError, setLocError] = useState<string | null>(null);
  const [locStatus, setLocStatus] = useState<"idle" | "loading" | "ready">("idle");

  useEffect(() => {
    fetch("/api/ping")
      .then((res) => res.json())
      .then(() => setBackendStatus("ok"))
      .catch(() => setBackendStatus("error"));
  }, []);

  const rankingExplanation = useMemo(() => {
    return {
      weights: [
        { name: "Tag/text match", note: "Matches query tags/keywords" },
        { name: "Distance", note: "Closer results score higher (if location provided)" },
        { name: "Filters", note: "Open-now, max distance, max price" },
      ],
    };
  }, []);

  function requestLocation() {
    setLocError(null);

    if (!("geolocation" in navigator)) {
      setLocError("Geolocation is not supported by this browser.");
      return;
    }

    setLocStatus("loading");
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setLat(pos.coords.latitude);
        setLon(pos.coords.longitude);
        setLocStatus("ready");
        // Refresh results now that we have coords
        runSearch(pos.coords.latitude, pos.coords.longitude);
      },
      (err) => {
        setLocStatus("idle");
        setLocError(err.message || "Failed to get location.");
      },
      {
        enableHighAccuracy: false,
        timeout: 10000,
        maximumAge: 60_000,
      }
    );
  }

  function clearLocation() {
    setLat(null);
    setLon(null);
    setLocStatus("idle");
    setLocError(null);
  }

  async function runSearch(forceLat?: number, forceLon?: number) {
    setLoading(true);
    setSearchError(null);

    const tagsCsv = toTagsCsv(query);
    if (!tagsCsv) {
      setResults([]);
      setLoading(false);
      setSearchError("Enter at least one keyword/tag to search.");
      return;
    }

    const params = new URLSearchParams();
    params.set("tags", tagsCsv);
    params.set("openNow", String(openNow));
    params.set("k", String(k));

    if (maxDist.trim()) params.set("maxDist", maxDist.trim());
    if (maxPrice.trim()) params.set("maxPrice", maxPrice.trim());

    // Include location if present (or explicitly passed in)
    const useLat = typeof forceLat === "number" ? forceLat : lat;
    const useLon = typeof forceLon === "number" ? forceLon : lon;
    if (typeof useLat === "number" && typeof useLon === "number") {
      params.set("lat", String(useLat));
      params.set("lon", String(useLon));
    }

    try {
      const res = await fetch(`/api/search?${params.toString()}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = (await res.json()) as SearchApiResponse;

      if (!data?.results || !Array.isArray(data.results)) {
        throw new Error("Unexpected response shape (expected { results: [...] })");
      }

      setResults(data.results);
    } catch (e: any) {
      setResults([]);
      setSearchError(`Backend search failed: ${e?.message ?? "unknown error"}`);
    } finally {
      setLoading(false);
    }
  }

  // Load something immediately so the UI isn't empty
  useEffect(() => {
    runSearch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const hasLocation = typeof lat === "number" && typeof lon === "number";

  return (
    <>
      {/* backend status */}
      <div className="mb-4 flex flex-wrap items-center gap-2 text-xs">
        <span className="font-medium">Backend:</span>
        <span
          className={[
            "rounded-full px-2 py-1",
            backendStatus === "ok" && "bg-emerald-50 text-emerald-700",
            backendStatus === "loading" && "bg-neutral-100 text-neutral-600",
            backendStatus === "error" && "bg-red-50 text-red-700",
          ]
            .filter(Boolean)
            .join(" ")}
        >
          {backendStatus}
        </span>

        <span className="ml-2 font-medium">Data:</span>
        <span className="rounded-full bg-sky-50 px-2 py-1 text-sky-700">
          backend
        </span>

        {searchError && (
          <span className="ml-2 rounded-full bg-red-50 px-2 py-1 text-red-700">
            {searchError}
          </span>
        )}
      </div>

      <div className="grid gap-6 lg:grid-cols-[320px_1fr]">
        {/* Left: filters */}
        <aside className="space-y-4">
          <div className="rounded-2xl border bg-white p-4 shadow-sm">
            <label className="text-sm font-semibold">Search (tags/keywords)</label>
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="mt-2 w-full rounded-xl border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-neutral-900/10"
              placeholder="coffee boba tea"
            />
            <p className="mt-2 text-xs text-neutral-500">
              Tip: spaces or commas both work (e.g. “boba tea” or “boba,tea”)
            </p>

            <button
              onClick={() => runSearch()}
              disabled={loading}
              className="mt-3 w-full rounded-xl bg-neutral-900 px-3 py-2 text-sm font-semibold text-white hover:bg-neutral-800 disabled:opacity-60"
            >
              {loading ? "Searching..." : "Search"}
            </button>
          </div>

          <FilterCard title="Filters">
            <div className="grid grid-cols-2 gap-2">
              <button
                onClick={() => setOpenNow((v) => !v)}
                className={[
                  "rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50",
                  openNow && "bg-neutral-900 text-white hover:bg-neutral-800",
                ]
                  .filter(Boolean)
                  .join(" ")}
              >
                Open now
              </button>

              <button
                onClick={() => setShowDetails((v) => !v)}
                className={[
                  "rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50",
                  showDetails && "bg-neutral-900 text-white hover:bg-neutral-800",
                ]
                  .filter(Boolean)
                  .join(" ")}
              >
                Grader mode
              </button>
            </div>

            {/* Location controls */}
            <div className="mt-2 space-y-2">
              <div className="grid grid-cols-2 gap-2">
                <button
                  onClick={requestLocation}
                  disabled={locStatus === "loading"}
                  className={[
                    "rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50",
                    hasLocation && "bg-neutral-900 text-white hover:bg-neutral-800",
                  ]
                    .filter(Boolean)
                    .join(" ")}
                >
                  {locStatus === "loading"
                    ? "Getting location..."
                    : hasLocation
                    ? "Location enabled"
                    : "Use my location"}
                </button>

                <button
                  onClick={() => {
                    clearLocation();
                    runSearch();
                  }}
                  disabled={!hasLocation}
                  className="rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50 disabled:opacity-60"
                >
                  Clear
                </button>
              </div>

              <div className="text-xs text-neutral-600">
                {hasLocation ? (
                  <span>
                    lat {lat!.toFixed(5)}, lon {lon!.toFixed(5)}
                  </span>
                ) : (
                  <span>Distances will be “unknown” until location is enabled.</span>
                )}
              </div>

              {locError && (
                <div className="text-xs text-red-700">{locError}</div>
              )}
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="text-xs text-neutral-500">Max distance (mi)</label>
                <input
                  value={maxDist}
                  onChange={(e) => setMaxDist(e.target.value)}
                  className="mt-1 w-full rounded-xl border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-neutral-900/10"
                  placeholder="e.g. 5"
                />
              </div>
              <div>
                <label className="text-xs text-neutral-500">Max price ($)</label>
                <input
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                  className="mt-1 w-full rounded-xl border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-neutral-900/10"
                  placeholder="e.g. 2"
                />
              </div>
            </div>

            <div>
              <label className="text-xs text-neutral-500">Top K results</label>
              <input
                type="number"
                min={1}
                max={100}
                value={k}
                onChange={(e) => setK(Number(e.target.value))}
                className="mt-1 w-full rounded-xl border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-neutral-900/10"
              />
            </div>
          </FilterCard>
        </aside>

        {/* Right: results */}
        <section className="space-y-4">
          <div className="rounded-2xl border bg-white p-4 shadow-sm">
            <div className="flex items-center justify-between gap-3">
              <h2 className="text-sm font-semibold">What we rank + how ranking works</h2>
              <span className="text-xs text-neutral-500">(Designed for grading clarity)</span>
            </div>
            <p className="mt-2 text-sm text-neutral-700">
              We rank POIs (places) by a heuristic scoring model. Each result gets a final
              score, plus a human-readable explanation of why it ranked where it did.
            </p>
            <ul className="mt-2 list-disc pl-5 text-sm text-neutral-700">
              {rankingExplanation.weights.map((w) => (
                <li key={w.name}>
                  <span className="font-medium">{w.name}:</span> {w.note}
                </li>
              ))}
            </ul>
          </div>

          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold">
              Results{" "}
              <span className="text-xs font-normal text-neutral-500">
                ({results.length})
              </span>
            </h2>
          </div>

          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {results.map((r, i) => (
              <PoiCard
                key={`${r.name}-${i}`}
                item={r}
                rank={i + 1}
                showDetails={showDetails}
              />
            ))}
          </div>
        </section>
      </div>
    </>
  );
}