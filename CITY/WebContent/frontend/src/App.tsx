import Navbar from "./components/Navbar";
import Search from "./pages/Search";

export default function App() {
  return (
    <div className="min-h-screen bg-neutral-50 text-neutral-900">
      {/* Anchor for "Home" */}
      <div id="top" />

      <Navbar />

      <main className="mx-auto max-w-6xl px-4 py-6">
        {/* Anchor for "Search" */}
        <div id="search" />

        <Search />

        {/* Anchor for "About" */}
        <div id="about" className="mt-10" />

        <section className="rounded-2xl border bg-white p-6 shadow-sm">
          <h2 className="text-lg font-semibold">About</h2>
          <p className="mt-2 text-sm text-neutral-700">
            CITY is a simple POI search + ranking demo. The frontend calls the Java backend
            via <code>/api/search</code>, and results include a score and an explanation of
            why each POI ranked where it did.
          </p>
        </section>
      </main>
    </div>
  );
}