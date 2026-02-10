import type { Poi } from "../data/mockPois";

function Stars({ rating }: { rating: number }) {
  const full = Math.round(rating); // 0..5 (simple)
  const total = 5;

  return (
    <div className="flex items-center gap-2">
      <div className="text-sm leading-none text-neutral-900">
        {"⭐".repeat(full) + "☆".repeat(total - full)}
      </div>
      <div className="text-xs text-neutral-500">{rating.toFixed(1)}</div>
    </div>
  );
}

export default function PoiCard({ poi }: { poi: Poi }) {
  return (
    <article className="rounded-2xl border bg-white p-4 shadow-sm hover:shadow transition">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="text-base font-semibold">{poi.name}</h3>
          <p className="mt-1 text-sm text-neutral-600">{poi.category}</p>
        </div>

        <span
          className={[
            "rounded-full px-2 py-1 text-xs font-medium",
            poi.openNow ? "bg-emerald-50 text-emerald-700" : "bg-neutral-100 text-neutral-700"
          ].join(" ")}
        >
          {poi.openNow ? "Open" : "Closed"}
        </span>
      </div>

      <div className="mt-3 flex items-center justify-between">
        <Stars rating={poi.rating} />
        <div className="text-xs text-neutral-500">{poi.price}</div>
      </div>

      <p className="mt-3 text-sm text-neutral-700">{poi.address}</p>

      <div className="mt-4 flex items-center justify-between">
        <div className="text-xs text-neutral-500">{poi.distanceMi.toFixed(1)} mi away</div>
        <button className="rounded-xl bg-neutral-900 px-3 py-2 text-xs font-semibold text-white hover:bg-neutral-800">
          View
        </button>
      </div>
    </article>
  );
}