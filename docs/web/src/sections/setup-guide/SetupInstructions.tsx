import { FaBluetooth } from 'react-icons/fa';
import { LuHardDriveDownload, LuSend } from 'react-icons/lu';
import { MdOutlineFileDownload, MdOutlinePhonelinkSetup } from 'react-icons/md';
import Button from '../../components/button/Button';
import type { IStep } from '../../components/steps/IStep';
import StepList from '../../components/steps/StepList';
import classes from './setup.module.css';

const setupSteps: IStep[] = [
    {
        step_number: 1,
        icon: <LuHardDriveDownload size={50} color="var(--color-secondary)" />,
        title: 'Download and Install',
        description: 'Download and install Clipbird on every device you want to sync.',
        cta: (
            <Button icon={<MdOutlineFileDownload size={20} />} href="#downloads">
                Download
            </Button>
        )
    },
    {
        step_number: 2,
        icon: <FaBluetooth size={50} color="var(--color-secondary)" />,
        title: 'Pair via Bluetooth',
        description:
            'Use your operating system\'s Bluetooth settings to pair your devices with each other.'
    },
    {
        step_number: 3,
        icon: <MdOutlinePhonelinkSetup size={50} color="var(--color-secondary)" />,
        title: 'Pair in Clipbird',
        description: 'Open Clipbird on both devices and pair them within the app.'
    },
    {
        step_number: 4,
        icon: <LuSend size={50} color="var(--color-secondary)" />,
        title: 'Copy & Send',
        description:
            'Copy text on any device, then send it — on Android, tap Send from the History screen or the notification; on Linux, click Send from the History screen or double-click the tray icon to open the compact history window and click Send.'
    }
];

export default function SetupInstructions() {
    return (
        <section className={classes.setup}>
            <h2>Get Started</h2>

            <main className={classes.setup__instructions}>
                <StepList steps={setupSteps} />
            </main>
        </section>
    );
}
