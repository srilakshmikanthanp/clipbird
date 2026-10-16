import { FaBluetooth } from 'react-icons/fa';
import { LuDownload, LuGitBranch } from 'react-icons/lu';
import ExternalLink from './ExternalLink';

const SNAPSHOT_RELEASE_URL = 'https://github.com/srilakshmikanthanp/clipbird/releases/tag/snapshot';
const LATEST_RELEASE_URL = 'https://github.com/srilakshmikanthanp/clipbird/releases/latest';

const button =
  'flex flex-1 items-center justify-center gap-2 rounded-lg px-5 py-3 font-medium transition hover:-translate-y-px sm:flex-none';

export default function Introduction() {
  return (
    <section>
      <span className="inline-flex items-center gap-2 rounded-full border border-line px-3 py-1 text-sm text-muted">
        <FaBluetooth className="text-primary" /> Works over Bluetooth · Free &amp; open source
      </span>

      <h1 className="my-5 text-5xl font-bold tracking-tight md:text-6xl">
        One you.
        <br />
        One clipboard.
        <br />
        <span className="text-primary">Every device.</span>
      </h1>

      <p className="text-lg text-muted">
        Copy on one device. Paste on another. Nothing in between.
      </p>

      <div className="mt-8 flex gap-3">
        <ExternalLink href={LATEST_RELEASE_URL} className={`${button} bg-primary text-white hover:brightness-110`}>
          <LuDownload /> Download
        </ExternalLink>
        <ExternalLink href={SNAPSHOT_RELEASE_URL} className={`${button} border border-line hover:border-muted`}>
          <LuGitBranch /> Snapshot
        </ExternalLink>
      </div>
    </section>
  );
}
