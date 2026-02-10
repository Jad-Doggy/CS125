import Navbar from "./components/Navbar";
import SearchMock from "./pages/SearchMock";

export default function App() {
  return (
    <div className="min-h-screen bg-neutral-50 text-neutral-900">
      <Navbar />
      <main className="mx-auto max-w-6xl px-4 py-6">
        <SearchMock />
      </main>
    </div>
  );
}
