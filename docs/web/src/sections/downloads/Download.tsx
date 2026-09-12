import { FaDownload, FaGithub } from 'react-icons/fa';
import Button from '../../components/button/Button';
import classes from './download.module.css';

export default function Download() {
    return (
        <section className={classes.download} id="downloads">
            <h2>Downloads</h2>
            <p>Get the latest release or the rolling snapshot build on GitHub.</p>
            <div className={classes.links}>
                <Button
                    icon={<FaDownload size={20} />}
                    href="https://github.com/srilakshmikanthanp/clipbird/releases/latest"
                    target="_blank">
                    Latest
                </Button>
                <Button
                    icon={<FaGithub size={20} />}
                    href="https://github.com/srilakshmikanthanp/clipbird/releases/tag/snapshot"
                    target="_blank">
                    Snapshot
                </Button>
            </div>
        </section>
    );
}
