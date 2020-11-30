import Button from "@material-ui/core/Button";
import React, { useEffect, useState } from "react";
import OfflineBoltIcon from "@material-ui/icons/OfflineBolt";
import { CHALLENGES_FETCH, TEAM_EXECUTE_CHALLENGE, TEAMS_FETCH } from '../actions';
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
import { useLocalStorage } from 'react-use';

const useStyles = makeStyles(theme => ({
    actionButton: { flex: '0 1 auto', marginLeft: theme.spacing(2) },
    actionButtonIcon: { marginRight: theme.spacing(2) },
    text: { marginBottom: theme.spacing(2) },
    select: { margin: theme.spacing(2, 0) }
}));

export function ExecuteChallengeButton({ team, challenge, isLoading, challenges, teams, executeChallenge, fetchChallenges, fetchTeams }) {
    const classes = useStyles();
    const [rememberedTeam, setRememberedTeam] = useLocalStorage('team', '');
    const [rememberedChallenge, setRememberedChallenge] = useLocalStorage('challenge', '');
    const [rememberedSecret, setRememberedSecret] = useLocalStorage('secret', '');
    const [state, setState] = useState({ open: false, team: team || rememberedTeam, challenge: challenge || rememberedChallenge, secret: rememberedSecret });

    useEffect(() => {
        fetchChallenges();
    }, [challenges.length]);

    useEffect(() => {
        if (!teams) fetchTeams();
    }, [teams.length]);

    if (isLoading) {
        return <Button key="action" disabled className={classes.actionButton} variant="outlined" color="secondary">
            <OfflineBoltIcon className={classes.actionButtonIcon}/>Execute Challenge
        </Button>;
    }

    const handleOpen = () => {
        setState((prevState) => ({ ...prevState, open: true }));
    };

    const handleClose = () => {
        setState((prevState) => ({ ...prevState, open: false }));
    };

    const handleTeamChange = (event) => {
        const team = event.target.value;
        setRememberedTeam(team);
        setState((prevState) => ({ ...prevState, team }));
    };

    const handleChallengeChange = (event) => {
        const challenge = event.target.value;
        setRememberedChallenge(challenge);
        setState((prevState) => ({ ...prevState, challenge }));
    };

    const handleSecretChange = (event) => {
        const secret = event.target.value;
        setRememberedSecret(secret);
        setState((prevState) => ({ ...prevState, secret }));
    };

    const handleSubmit = () => {
        executeChallenge(state.team, state.challenge, state.secret);
        handleClose()
    };

    function renderTeamSelectOption(team) {
        return <MenuItem key={team.name} value={team.name}>{team.name}</MenuItem>;
    }

    function renderChallengeSelectOption(challenge) {
        return <MenuItem key={challenge.id} value={challenge.id}>{challenge.name}</MenuItem>;
    }

    function isFormValid() {
        return state.challenge && state.team && state.secret;
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
                <Select name="team" disabled={!!team} displayEmpty value={state.team} onChange={handleTeamChange} fullWidth
                        className={classes.select}>
                    <MenuItem value="" disabled>Select Team</MenuItem>
                    {teams.map(team => renderTeamSelectOption(team))}
                </Select>
                <Select name="challenge" disabled={!!challenge} displayEmpty value={state.challenge}
                        className={classes.select} onChange={handleChallengeChange} fullWidth>
                    <MenuItem value="" disabled>Select Challenge</MenuItem>
                    {challenges.map(challenge => renderChallengeSelectOption(challenge))}
                </Select>
                <TextField value={state.secret}
                           type="password"
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
    teams: state.teams.data,
    challenges: state.challenges.data,
    isLoading: state.challenges.isLoading
});

const mapDispatchToProps = dispatch => bindActionCreators({
    fetchTeams: TEAMS_FETCH,
    fetchChallenges: CHALLENGES_FETCH,
    executeChallenge: TEAM_EXECUTE_CHALLENGE
}, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(ExecuteChallengeButton);