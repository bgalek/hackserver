import React, { Component } from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';
import { CHALLENGES_FETCH } from "../actions";
import { push } from "connected-react-router";
import { connect } from "react-redux";
import Loader from "./Teams";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import AccessAlarmsIcon from '@material-ui/icons/AccessAlarms';

const styles = theme => ({});

class Challenges extends Component {

    async componentDidMount() {
        this.props.fetchChallenges();
    }

    render() {
        const { challenges, isLoading, navigateTo } = this.props;
        if (isLoading) return <Loader/>;
        return [
            <Typography key="title" variant="h4" gutterBottom>
                Challenges
            </Typography>,
            <List key="teams-list">
                {challenges.map((challenge, index) =>
                    <ListItem button key={challenge.name + index}
                              onClick={() => navigateTo(`/challenge/${challenge.name}`)}>
                        <ListItemAvatar>
                            <Avatar>
                                <AccessAlarmsIcon/>
                            </Avatar>
                        </ListItemAvatar>
                        <ListItemText primary={challenge.name}/>
                    </ListItem>
                )}
            </List>
        ];
    }
}

Challenges.defaultProps = { teams: [] };

Challenges.propTypes = {
    classes: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
    challenges: state.challenges.data,
    isLoading: state.teams.isLoading
});

const mapDispatchToProps = (dispatch) => ({
    fetchChallenges: () => dispatch(CHALLENGES_FETCH),
    navigateTo: (route) => dispatch(push(route))
});

const connectedChallenges = connect(mapStateToProps, mapDispatchToProps)(Challenges);
export default withStyles(styles)(connectedChallenges);
