import Button from '@material-ui/core/Button';
import React, { useEffect, useState } from 'react';
import ComputerIcon from '@material-ui/icons/Computer';
import { CHALLENGES_FETCH, TEAM_SEND_EXAMPLE, TEAMS_FETCH } from '../actions';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogActions from '@material-ui/core/DialogActions';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import { bindActionCreators } from 'redux';
import { useLocalStorage } from 'react-use';

const useStyles = makeStyles(theme => ({
    actionButton: { flex: '0 1 auto' },
    actionButtonIcon: { marginRight: theme.spacing(2) },
    text: { marginBottom: theme.spacing(2) },
    select: { margin: theme.spacing(2, 0) }
}));

export function TestTeamButton({ team, challenge, isLoading, challenges = [], teams = [], sendExample, fetchChallenges, fetchTeams }) {
    const classes = useStyles();
    const [rememberedTeam, setRememberedTeam] = useLocalStorage('team', '');
    const [rememberedChallenge, setRememberedChallenge] = useLocalStorage('challenge', '');
    const [state, setState] = useState({ open: false, team: team || rememberedTeam, challenge: challenge || rememberedChallenge });

    useEffect(() => {
        if (!challenges) fetchChallenges();
    }, [challenges.length]);

    useEffect(() => {
        if (!teams) fetchTeams();
    }, [teams.length]);

    if (isLoading) {
        return <Button key="action" disabled className={classes.actionButton} variant="outlined" color="secondary">
            <ComputerIcon className={classes.actionButtonIcon}/>Test me
        </Button>;
    }

    const handleOpen = () => {
        setState((prevState) => ({ ...prevState, open: true }));
    };

    const handleClose = () => {
        setState((prevState) => ({ ...prevState, open: false }));
    };

    const handleChallengeChange = (event) => {
        const challenge = event.target.value;
        setRememberedChallenge(challenge);
        setState((prevState) => ({ ...prevState, challenge }));
    };

    const handleTeamChange = (event) => {
        const team = event.target.value;
        setRememberedTeam(team);
        setState((prevState) => ({ ...prevState, team }));
    };

    const handleSubmit = () => {
        sendExample(state.team, state.challenge);
        handleClose();
    };

    function renderTeamSelectOption(team) {
        return <MenuItem key={team.name} value={team.name}>{team.name}</MenuItem>;
    }

    function renderChallengeSelectOption(challenge) {
        return <MenuItem key={challenge.id} value={challenge.id}>{challenge.name}</MenuItem>;
    }

    function isFormValid() {
        return state.team && state.challenge;
    }

    return [
        <Button key="action" onClick={handleOpen} className={classes.actionButton} variant="text"
                color="secondary">
            <ComputerIcon className={classes.actionButtonIcon}/>
            Test me
        </Button>,
        <Dialog key="dialog" open={state.open} onClose={handleClose}>
            <DialogTitle>Test Challenge</DialogTitle>
            <DialogContent>
                <DialogContentText className={classes.text}>
                    To check your team, please select challenge you want to be checked with.
                </DialogContentText>
                <Select name="team" disabled={!!team} displayEmpty value={state.team} onChange={handleTeamChange} fullWidth
                        className={classes.select}>
                    <MenuItem value="" disabled>Select Team</MenuItem>
                    {teams.map(team => renderTeamSelectOption(team))}
                </Select>
                <Select name="challenge" disabled={!!challenge} displayEmpty value={state.challenge} onChange={handleChallengeChange} fullWidth
                        className={classes.select}>
                    <MenuItem value="" disabled>Select Challenge</MenuItem>
                    {challenges.map(challenge => renderChallengeSelectOption(challenge))}
                </Select>
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
    sendExample: TEAM_SEND_EXAMPLE
}, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(TestTeamButton);