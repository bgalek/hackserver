import React, { Component } from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';
import { connect } from "react-redux";
import Loader from "../layout/Loader";
import Avatar from "@material-ui/core/Avatar";
import Grid from "@material-ui/core/Grid";
import { TEAMS_FETCH } from "../actions";

const styles = theme => ({
    avatar: {
        margin: 10,
        width: 128,
        height: 128,
    },
});

class TeamDetail extends Component {

    async componentDidMount() {
        if (!this.props.team) {
            this.props.fetchTeams();
        }
    }

    render() {
        const { classes, team, isLoading } = this.props;
        if (isLoading) return <Loader/>;
        return [
            <Typography key="title" variant="h4" gutterBottom>
                Teams: <strong>{team.name}</strong>
            </Typography>,
            <Grid key="details" container alignItems="center">
                <Avatar alt={team.name} src={team.avatar} className={classes.avatar}/>
            </Grid>
        ];
    }
}

TeamDetail.propTypes = {
    classes: PropTypes.object.isRequired,
};

const mapStateToProps = (state, props) => {
    return ({
        team: state.teams.data.find(team => team.name = props.match.params.id),
        isLoading: state.teams.isLoading
    });
};

const mapDispatchToProps = (dispatch) => ({
    fetchTeams: () => dispatch(TEAMS_FETCH)
});

const connectedTeamDetail = connect(mapStateToProps, mapDispatchToProps)(TeamDetail);
export default withStyles(styles)(connectedTeamDetail);
