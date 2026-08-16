import { BiDonateHeart } from 'react-icons/bi';
import { FaGithub } from 'react-icons/fa';
import { IoIosGitPullRequest } from 'react-icons/io';
import { LuStar } from 'react-icons/lu';
import { VscGitPullRequestGoToChanges, VscRepoForked } from 'react-icons/vsc';
import Button from '../../components/button/Button';
import Hyperlink from '../../components/hyperlink/Hyperlink';
import type { IStep } from '../../components/steps/IStep';
import StepList from '../../components/steps/StepList';
import classes from './contribute.module.css';

const contributeSteps: IStep[] = [
    {
        step_number: 1,
        icon: <VscRepoForked size={50} color="var(--color-secondary)" />,
        title: 'Fork the Repository',
        description: 'Fork the Clipbird repository on GitHub to get started.',
        cta: (
            <div className={classes.contribute__links}>
                <Hyperlink
                    icon={<FaGithub />}
                    target="_blank"
                    text="clipbird"
                    href="https://github.com/srilakshmikanthanp/clipbird"
                />
            </div>
        )
    },
    {
        step_number: 2,
        icon: <VscGitPullRequestGoToChanges size={50} color="var(--color-secondary)" />,
        title: 'Make Changes',
        description: 'Make the desired changes, add new features, or fix bugs in your forked repository.'
    },
    {
        step_number: 3,
        icon: <IoIosGitPullRequest size={50} color="var(--color-secondary)" />,
        title: 'Create a PR',
        description:
            'Submit a pull request to the main Clipbird repository. Provide a clear description of your changes.'
    }
];

const supportSteps: IStep[] = [
    {
        step_number: 1,
        icon: <LuStar size={50} color="var(--color-secondary)" />,
        title: 'Star the Repository',
        description: 'Show your support by starring the Clipbird repository on GitHub.',
        cta: (
            <div className={classes.contribute__links}>
                <Hyperlink
                    icon={<FaGithub />}
                    target="_blank"
                    text="clipbird"
                    href="https://github.com/srilakshmikanthanp/clipbird"
                />
            </div>
        )
    },
    {
        step_number: 2,
        icon: <BiDonateHeart size={50} color="var(--color-secondary)" />,
        title: 'Sponsor the Project',
        description: 'If you love Clipbird and want to support its development, consider becoming a sponsor.',
        cta: (
            <Button icon={<BiDonateHeart />} href="https://donate.srilakshmikanthanp.com/">
                Donate
            </Button>
        )
    }
];

export default function Contribute() {
    return (
        <section className={classes.contribute}>
            <h2>Contribute</h2>
            <p>
                Clipbird is a free and open-source project, and we welcome contributions from the community. Whether you
                want to report an issue, add a feature, or fix a bug, your contributions are valuable to us.
            </p>

            <div className={classes.contribute__option}>
                <h4>How to contribute</h4>

                <div className={classes.contribute__steps}>
                    <StepList steps={contributeSteps} />
                </div>
            </div>

            <hr className={classes.contribute__separator} />

            <div className={classes.contribute__option}>
                <h4>Other ways to support</h4>

                <div className={classes.contribute__steps}>
                    <StepList steps={supportSteps} />
                </div>
            </div>
        </section>
    );
}
