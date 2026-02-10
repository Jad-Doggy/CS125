type Props = {
  title: string;
  children: React.ReactNode;
};

export default function FilterCard({ title, children }: Props) {
  return (
    <section className="rounded-2xl border bg-white p-4 shadow-sm">
      <h2 className="text-sm font-semibold">{title}</h2>
      <div className="mt-3 space-y-3">{children}</div>
    </section>
  )
}