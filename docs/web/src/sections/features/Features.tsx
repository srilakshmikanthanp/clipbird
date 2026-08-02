import { FaBluetooth } from 'react-icons/fa';
import { GoHistory, GoShieldLock } from 'react-icons/go';
import { HiOutlineClipboardDocumentList } from 'react-icons/hi2';
import Card from '../../components/card/Card';
import classes from './features.module.css';

export default function Features() {
    return (
        <section className={classes.features}>
            <h2>Features</h2>
            <div className={classes.features__list}>
                <Card>
                    <div>
                        <HiOutlineClipboardDocumentList size={60} />
                    </div>
                    <h3>Clipboard Sync</h3>
                    <p>Instantly send clipboard content from one device to all your devices with a single tap.</p>
                </Card>

                <Card>
                    <div>
                        <FaBluetooth size={60} />
                    </div>
                    <h3>Bluetooth Transfer</h3>
                    <p>Share clipboard data directly over Bluetooth — no Wi-Fi, no router, no internet needed.</p>
                </Card>

                <Card>
                    <div>
                        <GoShieldLock size={60} />
                    </div>
                    <h3>Secure Encryption</h3>
                    <p>Clipboard data is encrypted in transit — no one in between can read what you send.</p>
                </Card>

                <Card>
                    <div>
                        <GoHistory size={60} />
                    </div>
                    <h3>Clipboard History</h3>
                    <p>Access a history of your clipboard items and send any past entry to connected devices.</p>
                </Card>
            </div>
        </section>
    );
}
