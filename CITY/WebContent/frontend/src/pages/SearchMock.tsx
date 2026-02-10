import FilterCard from "../components/FilterCard";
import PoiCard from "../components/PoiCard";
import { mockPois } from "../data/mockPois";

export default function SearchMock() {
  return (
    <div className="grid gap-6 lg:grid-cols-[320px_1fr]">
      {/* Left: filters */}
      <aside className="space-y-4">
        <div className="rounded-2xl border bg-white p-4 shadow-sm">
          <label className="text-sm font-semibold">Search</label>
          <input
            className="mt-2 w-full rounded-xl border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-neutral-900/10"
            placeholder="Coffee, parks, restaurants..."
          />
          <p className="mt-2 text-xs text-neutral-500">
            (No behavior yet — layout only)
          </p>
        </div>

        <FilterCard title="Filters">
          <div className="grid grid-cols-2 gap-2">
            <button className="rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50">Open now</button>
            <button className="rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50">Near me</button>
            <button className="rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50">$</button>
            <button className="rounded-xl border px-3 py-2 text-sm hover:bg-neutral-50">$$</button>
          </div>

          <div>
            <div className="text-xs font-medium text-neutral-700">Category</div>
            <div className="mt-2 flex flex-wrap gap-2">
              {["Cafe", "Restaurant", "Shop", "Park"].map((c) => (
                <span key={c} className="rounded-full border bg-white px-3 py-1 text-xs">
                  {c}
                </span>
              ))}
            </div>
          </div>

          <div>
            <div className="text-xs font-medium text-neutral-700">Radius</div>
            <div className="mt-2 rounded-xl border bg-neutral-50 px-3 py-2 text-sm text-neutral-600">
              5 miles (mock)
            </div>
          </div>
        </FilterCard>

        <FilterCard title="Map">
          <div className="aspect-[4/3] w-full rounded-xl border bg-neutral-100 grid place-items-center text-sm text-neutral-500">
            Map placeholder
          </div>
        </FilterCard>
      </aside>

      {/* Right: results */}
      <section>
        <div className="flex items-end justify-between gap-4">
          <div>
            <h1 className="text-xl font-semibold">Results</h1>
            <p className="mt-1 text-sm text-neutral-600">
              Showing mocked POIs — replace with API later.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs text-neutral-500">Sort</span>
            <button className="rounded-xl border bg-white px-3 py-2 text-sm hover:bg-neutral-50">
              Rating
            </button>
          </div>
        </div>

        <div className="mt-5 grid gap-4 md:grid-cols-2">
          {mockPois.map((p) => (
            <PoiCard key={p.id} poi={p} />
          ))}
        </div>
      </section>
    </div>
  );
}
