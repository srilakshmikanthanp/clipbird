import { FaBluetooth } from 'react-icons/fa';
import { MdOutlineFileDownload } from 'react-icons/md';
import Button from '../../components/button/Button';
import Pill from '../../components/pill/Pill';
import classes from './hero.module.css';

export default function Hero() {
    return (
        <section className={classes.hero}>
            <Pill icon={<FaBluetooth />}>No cloud · No account · No router</Pill>
            <h1>One you. One clipboard. Every device.</h1>
            <p className={classes.hero__primary}>
                Copy on one device. Paste on another. Nothing in between.
            </p>
            <p className={classes.hero__description}>
                Clipbird syncs clipboard content directly between your devices — no cloud, no router,
                no account required.
            </p>

            <Button icon={<MdOutlineFileDownload size={20} />} href="#downloads">
                Download
            </Button>
        </section>
    );
}
