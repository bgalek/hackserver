import Button from "@material-ui/core/Button";
import React, { useEffect, useState } from "react";
import OfflineBoltIcon from "@material-ui/icons/OfflineBolt";
import { CHALLENGES_FETCH, TEAM_EXECUTE_CHALLENGE } from "../actions";
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
import TextField from "@material-ui/core/TextField";

const useStyles = makeStyles(theme => ({
    actionButton: { flex: '0 1 auto', marginLeft: theme.spacing(2) },
    actionButtonIcon: { marginRight: theme.spacing(2) },
    text: { marginBottom: theme.spacing(2) }
}));

export function ExecuteChallengeButton({ isLoading, challenges, team, executeChallenge, fetchChallenges }) {
    const classes = useStyles();
    const [state, setState] = useState({ open: false, challenge: '', secret: '' });

    useEffect(() => {
        fetchChallenges();
    }, [challenges.length]);

    if (isLoading || !team.health) {
        return <Button key="action" disabled className={classes.actionButton} variant="outlined" color="secondary">
            <OfflineBoltIcon className={classes.actionButtonIcon}/>Execute Challenge
        </Button>;
    }

    const handleOpen = () => {
        setState((prevState) => ({ ...prevState, open: true }));
    };

    const handleClose = () => {
        setState((prevState) => ({ ...prevState, open: false, challenge: '' }));
    };

    const handleChallengeChange = (event) => {
        const challenge = event.target.value;
        setState((prevState) => ({ ...prevState, challenge }));
    };

    const handleSecretChange = (event) => {
        const secret = event.target.value;
        setState((prevState) => ({ ...prevState, secret }));
    };

    const handleSubmit = () => {
        executeChallenge(team.name, state.challenge, state.secret);
        handleClose()
    };

    function renderSelectOption(challenge) {
        return <MenuItem key={challenge.id} value={challenge.id}>{challenge.name}</MenuItem>;
    }

    function isFormValid() {
        return state.challenge && state.secret;
    }

    return [
        <Button key="action" onClick={handleOpen} className={classes.actionButton} variant="contained"
                color="secondary">
            <OfflineBoltIcon className={classes.actionButtonIcon}/>
            Execute Challenge
        </Button>,
        <Dialog key="dialog" open={state.open} onClose={handleClose}>
            <DialogTitle>Execute Challenge</DialogTitle>
            <DialogContent>
                <DialogContentText className={classes.text}>
                        Select challenge you want to be executed on Your team.
                        <br />
                        Use Team Secret from registration!
                </DialogContentText>
                <Select name="challenge" displayEmpty value={state.challenge}
                        className={classes.selectEmpty} onChange={handleChallengeChange} fullWidth>
                    <MenuItem value="" disabled>Select Challenge</MenuItem>
                    {challenges.map(challenge => renderSelectOption(challenge))}
                </Select>
                <TextField value={state.secret}
                           placeholder="6cba47f8-6a0b-48ff-94bd-a3bbb32d6e5d"
                           onChange={handleSecretChange}
                           fullWidth
                           margin="normal"
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color="primary">
                    Cancel
                </Button>
                <Button disabled={!isFormValid()} variant="contained" onClick={handleSubmit} color="primary">
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
    executeChallenge: TEAM_EXECUTE_CHALLENGE
}, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(ExecuteChallengeButton);