import { FaGithub } from 'react-icons/fa';
import classes from './header.module.css';

export default function Header() {
    return (
        <header className={classes.header}>
            <img height={40} width={40} src="/logo.png" alt="Clipbird Logo" />
            <a href="https://github.com/srilakshmikanthanp/clipbird">
                <FaGithub size={40} color="white" />
            </a>
        </header>
    );
}
