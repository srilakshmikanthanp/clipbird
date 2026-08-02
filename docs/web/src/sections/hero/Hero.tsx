import { FaBluetooth } from 'react-icons/fa';
import { MdOutlineFileDownload } from 'react-icons/md';
import Button from '../../components/button/Button';
import Pill from '../../components/pill/Pill';
import classes from './hero.module.css';

export default function Hero() {
    return (
        <section className={classes.hero}>
            <Pill icon={<FaBluetooth />}>No internet · No account · No server</Pill>
            <h1>Same you, why different clipboards?</h1>
            <p className={classes.hero__primary}>
                Share your clipboard between devices over Bluetooth — privately and securely.
            </p>
            <p className={classes.hero__description}>
                ClipBird syncs clipboard content between your devices using Bluetooth, with no cloud, no Wi-Fi, and no
                account required.
            </p>

            <Button icon={<MdOutlineFileDownload size={20} />} href="#downloads">
                Download
            </Button>
        </section>
    );
}
