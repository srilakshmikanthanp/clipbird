import classes from './footer.module.css';

export default function Footer() {
    return <footer className={classes.footer}>© {new Date().getFullYear()} Clipbird. All rights reserved.</footer>;
}
