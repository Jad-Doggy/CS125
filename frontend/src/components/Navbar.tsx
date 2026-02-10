export default function Navbar() {
  return (
    <header className="sticky top-0 z-10 border-b bg-white/80 backdrop -blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
        <div className="flex items-center gap-2">
          <div className="h-9 w-9 rounded-xl bg-neutral-900" />
          <div className="leading-tight">
            <div className="text-sm font-semibold">CITY</div>
            <div className="text-xs text-neutral-500">Layout</div>
          </div>
        </div>

        <nav className="flex items-center gap-2">
          <button className="rounded-xl px-3 py-2 text-sm font-medium text-neutral-700 hover:bg-neutral-100">
            Home
          </button>
          <button className="rounded-xl px-3 py-2 text-sm font-medium text-neutral-700 hover:bg-neutral-100">
            Search
          </button>
          <button className="rounded-xl px-3 py-2 text-sm font-medium text-neutral-700 hover:bg-neutral-100">
            About
          </button>
        </nav>
      </div>
    </header>
  );
}