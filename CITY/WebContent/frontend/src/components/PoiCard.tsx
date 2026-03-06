function fmtMiles(mi: number | null) {
  if (mi === null || Number.isNaN(mi)) return "—";
  return `${mi.toFixed(2)} mi`;
}

type SearchApiItem = {
  name: string;
  distanceMiles: number | null;
  tagMatches: number;
  score: number;
  why: string[];
};

export default function PoiCard({
  item,
  rank,
  showDetails,
}: {
  item: SearchApiItem;
  rank: number;
  showDetails: boolean;
}) {
  return (
    <article className="rounded-2xl border bg-white p-4 shadow-sm hover:shadow transition">
      <div className="flex items-start justify-between gap-3">
        <div>
          <div className="text-xs text-neutral-500">#{rank}</div>
          <h3 className="text-base font-semibold">{item.name}</h3>
        </div>

        <div className="text-right">
          <div className="text-xs text-neutral-500">Score</div>
          <div className="text-sm font-semibold">{item.score.toFixed(4)}</div>
        </div>
      </div>

      <div className="mt-3 flex items-center justify-between text-sm text-neutral-700">
        <div>
          <span className="text-xs text-neutral-500">Distance</span>
          <div className="font-medium">{fmtMiles(item.distanceMiles)}</div>
        </div>

        <div className="text-right">
          <span className="text-xs text-neutral-500">Tag matches</span>
          <div className="font-medium">{item.tagMatches}</div>
        </div>
      </div>

      {showDetails && (
        <div className="mt-4 rounded-xl bg-neutral-50 p-3">
          <div className="text-xs font-semibold text-neutral-700">Why this ranked here</div>
          <ul className="mt-2 list-disc space-y-1 pl-5 text-xs text-neutral-700">
            {item.why?.length ? (
              item.why.map((w, idx) => <li key={idx}>{w}</li>)
            ) : (
              <li>No explanation provided.</li>
            )}
          </ul>
        </div>
      )}
    </article>
  );
}