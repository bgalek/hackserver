import Button from "@material-ui/core/Button";
import React, { useEffect, useState } from "react";
import ComputerIcon from "@material-ui/icons/Computer";
import { CHALLENGES_FETCH, TEAM_SEND_EXAMPLE } from "../actions";
import { connect } from "react-redux";
import { makeStyles } from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import { bindActionCreators } from "redux";

const useStyles = makeStyles(theme => ({
    actionButton: { flex: '0 1 auto' },
    actionButtonIcon: { marginRight: theme.spacing(1) },
    text: { marginBottom: theme.spacing(2) }
}));

export function TestTeamButton({ isLoading, challenges, team, sendExample, fetchChallenges }) {
    const classes = useStyles();
    const [state, setState] = useState({ open: false, challenge: '' });

    useEffect(() => {
        fetchChallenges();
    }, [challenges.length]);

    if (isLoading || !team.health) {
        return <Button key="action" disabled className={classes.actionButton} variant="contained" color="secondary">
            <ComputerIcon className={classes.actionButtonIcon}/>Test me
        </Button>;
    }

    const handleOpen = () => {
        setState((prevState) => ({ ...prevState, open: true }));
    };

    const handleClose = () => {
        setState((prevState) => ({ ...prevState, open: false, challenge: '' }));
    };

    const handleChallengeChange = (event) => {
        setState((prevState) => ({ ...prevState, challenge: event.target.value }));
    };

    const handleSubmit = () => {
        sendExample(team.name, state.challenge);
        handleClose()
    };

    function renderSelectOption(challenge) {
        return <MenuItem key={challenge.id} value={challenge.id}>{challenge.name}</MenuItem>;
    }

    function isFormValid() {
        return !state.challenge;
    }

    return [
        <Button key="action" onClick={handleOpen} className={classes.actionButton} variant="contained"
                color="secondary">
            <ComputerIcon className={classes.actionButtonIcon}/>
            Test me
        </Button>,
        <Dialog key="dialog" open={state.open} onClose={handleClose}>
            <DialogTitle>Select Challenge</DialogTitle>
            <DialogContent>
                <DialogContentText className={classes.text}>
                    To check your team, please select challenge you want to be checked with.
                </DialogContentText>
                <Select name="challenge" displayEmpty value={state.challenge}
                        className={classes.selectEmpty} onChange={handleChallengeChange} fullWidth>
                    <MenuItem value="" disabled>Select Challenge</MenuItem>
                    {challenges.map(challenge => renderSelectOption(challenge))}
                </Select>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color="primary">
                    Cancel
                </Button>
                <Button disabled={isFormValid()} variant="contained" onClick={handleSubmit} color="primary">
                    Start
                </Button>
            </DialogActions>
        </Dialog>];
}

const mapStateToProps = (state) => ({
    challenges: state.challenges.data,
    isLoading: state.challenges.isLoading
});

const mapDispatchToProps = dispatch => bindActionCreators({
    fetchChallenges: CHALLENGES_FETCH,
    sendExample: TEAM_SEND_EXAMPLE
}, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(TestTeamButton);