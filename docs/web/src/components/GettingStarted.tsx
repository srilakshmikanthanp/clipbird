interface Step {
  title: string;
  text: string;
}

const steps: Step[] = [
  {
    title: 'Install',
    text: 'Install Clipbird on every device you want to sync.'
  },
  {
    title: 'Pair over Bluetooth',
    text: 'Pair the devices in your OS Bluetooth settings.'
  },
  {
    title: 'Pair in Clipbird',
    text: 'Pick the device in the app and confirm the code on both.'
  },
  {
    title: 'Copy & send',
    text: 'Copy, then hit Send. On Android from the notification or History, on Linux from History or the tray.'
  }
];

export default function GettingStarted() {
  return (
    <section>
      <h2 className="mb-5 text-center text-xs font-semibold tracking-wider text-muted uppercase">Get started</h2>
      <ol className="grid gap-6 md:grid-cols-4 md:gap-0">
        {steps.map((s, i) => (
          <li key={s.title} className="flex gap-3 md:flex-col md:text-center">
            <div className="flex flex-col items-center gap-2 md:flex-row">
              <span className={`hidden h-px flex-1 md:block ${i > 0 ? 'bg-line' : ''}`} />
              <span className="grid size-7 shrink-0 place-items-center rounded-full bg-primary-soft text-xs font-semibold text-primary">
                {i + 1}
              </span>
              <span className={`-mb-4 w-px flex-1 md:mb-0 md:h-px ${i < steps.length - 1 ? 'bg-line' : ''}`} />
            </div>
            <div className="pt-1 md:px-4 md:pt-0">
              <h3 className="text-sm font-semibold">{s.title}</h3>
              <p className="text-sm text-muted">{s.text}</p>
            </div>
          </li>
        ))}
      </ol>
    </section>
  );
}
