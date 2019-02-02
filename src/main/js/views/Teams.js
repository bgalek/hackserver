import React, { Component } from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import FolderIcon from '@material-ui/icons/Folder';
import ListItemText from "@material-ui/core/ListItemText";
import { connect } from "react-redux";
import { TEAMS_FETCH } from "../actions";
import Loader from "../layout/Loader";
import { push } from "connected-react-router";

const styles = theme => ({});

class Teams extends Component {

    async componentDidMount() {
        this.props.fetchTeams();
    }

    render() {
        const { teams, isLoading, navigateTo } = this.props;
        if (isLoading) return <Loader/>;
        return [
            <Typography key="title" variant="h4" gutterBottom>
                Teams
            </Typography>,
            <List key="teams-list">
                {teams.map((team, index) =>
                    <ListItem button key={team.name + index} onClick={() => navigateTo(`/team/${team.name}`)}>
                        <ListItemAvatar>
                            <Avatar>
                                <FolderIcon/>
                            </Avatar>
                        </ListItemAvatar>
                        <ListItemText primary={team.name}/>
                    </ListItem>
                )}
            </List>
        ];
    }
}

Teams.defaultProps = { teams: [] };

Teams.propTypes = {
    classes: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
    teams: state.teams.data,
    isLoading: state.teams.isLoading
});

const mapDispatchToProps = (dispatch) => ({
    fetchTeams: () => dispatch(TEAMS_FETCH),
    navigateTo: (route) => dispatch(push(route))
});

const connectedTeams = connect(mapStateToProps, mapDispatchToProps)(Teams);
export default withStyles(styles)(connectedTeams);
