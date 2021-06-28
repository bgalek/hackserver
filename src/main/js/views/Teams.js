import React, { useEffect, useState } from 'react';
import { makeStyles } from "@material-ui/core";
import Typography from '@material-ui/core/Typography';
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import { connect } from "react-redux";
import { TEAMS_FETCH } from "../actions";
import Loader from "../components/Loader";
import Dialog from "@material-ui/core/Dialog";
import TeamDetail from "./TeamDetail";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import TeamHealthIndicator from "../components/TeamHealthIndicator";
import { bindActionCreators } from "redux";

const useStyles = makeStyles(theme => ({
    dialog: {
        backgroundColor: '#fafafa',
    },
    healthChip: {
        marginRight: theme.spacing(2)
    }
}));

function Teams({ fetchTeams, teams = [], isLoading }) {
    const classes = useStyles();
    const [modal, setModal] = useState({ open: false, selected: null });
    useEffect(() => {
        fetchTeams();
    }, [teams.length]);
    if (isLoading) return <Loader/>;
    console.log(teams);
    return [
        <Typography key="title" variant="h4" gutterBottom>
            Teams
        </Typography>,
        <List key="teams-list">
            {teams.map((team, index) =>
                <ListItem button key={team.name + index} onClick={() => setModal({ open: true, selected: team })}>
                    <ListItemAvatar>
                        <Avatar alt={team.name} src={team.avatar}/>
                    </ListItemAvatar>
                    <ListItemText primary={team.name} secondary={team.address + ":" + team.port}/>
                    <ListItemSecondaryAction className={classes.healthChip}>
                        <TeamHealthIndicator health={team.health}/>
                    </ListItemSecondaryAction>
                </ListItem>
            )}
        </List>,
        <Dialog key="team-details-modal" fullScreen open={modal.open}
                onClose={() => setModal({ open: false, selected: modal.selected })}
                className={classes.dialog}>
            <TeamDetail onClose={() => setModal({ open: false, selected: modal.selected })} team={modal.selected}/>
        </Dialog>
    ];
}

const mapStateToProps = (state) => ({
    teams: state.teams.data,
    isLoading: state.teams.isLoading
});

const mapDispatchToProps = dispatch => bindActionCreators({ fetchTeams: TEAMS_FETCH }, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(Teams);
