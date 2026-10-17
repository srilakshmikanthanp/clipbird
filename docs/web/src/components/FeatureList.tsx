import type { ReactNode } from 'react';
import { FaBluetooth } from 'react-icons/fa';
import { LuClipboardList, LuHistory, LuMonitorSmartphone, LuSend, LuShieldCheck } from 'react-icons/lu';

interface Feature {
  icon: ReactNode;
  title: string;
  text: string;
}

const features: Feature[] = [
  {
    icon: <LuClipboardList />,
    title: 'Text & images',
    text: 'Plain text, rich text and pictures.'
  },
  {
    icon: <FaBluetooth />,
    title: 'Bluetooth direct',
    text: 'No Wi-Fi, router or internet needed.'
  },
  {
    icon: <LuShieldCheck />,
    title: 'Encrypted',
    text: 'Every transfer, end to end.'
  },
  {
    icon: <LuSend />,
    title: 'All devices at once',
    text: 'One Send reaches every connected device.'
  },
  {
    icon: <LuMonitorSmartphone />,
    title: 'Cross-platform',
    text: 'Android and Linux. Windows coming soon.'
  },
  {
    icon: <LuHistory />,
    title: 'History',
    text: 'Your last 10 received items.'
  }
];

export default function FeatureList() {
  return (
    <ul className="grid grid-cols-2 gap-6 rounded-2xl border border-line bg-surface p-6 shadow-lg">
      {features.map(f => (
        <li key={f.title} className="flex flex-col gap-3 sm:flex-row">
          <span className="grid size-9 shrink-0 place-items-center rounded-lg bg-primary-soft text-primary">
            {f.icon}
          </span>
          <div>
            <h3 className="text-sm font-semibold">{f.title}</h3>
            <p className="text-sm text-muted">{f.text}</p>
          </div>
        </li>
      ))}
    </ul>
  );
}
