import React, { Component } from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';
import { connect } from "react-redux";
import Loader from "../layout/Loader";

const styles = theme => ({});

class TeamDetail extends Component {
    render() {
        const { team, isLoading } = this.props;
        if (isLoading) return <Loader/>;
        return [
            <Typography key="title" variant="h4" gutterBottom>
                Team
            </Typography>,
            <p key="json">{JSON.stringify(team)}</p>
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

const mapDispatchToProps = (dispatch) => ({});

const connectedTeamDetail = connect(mapStateToProps, mapDispatchToProps)(TeamDetail);
export default withStyles(styles)(connectedTeamDetail);
