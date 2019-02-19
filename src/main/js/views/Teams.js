import React, { Component } from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import { connect } from "react-redux";
import { TEAMS_FETCH } from "../actions";
import Loader from "../layout/Loader";
import Dialog from "@material-ui/core/Dialog";
import Slide from "@material-ui/core/Slide";
import TeamDetail from "./TeamDetail";

const styles = theme => ({
    dialog: {
        backgroundColor: '#fafafa',
    }
});

class Teams extends Component {

    constructor(props) {
        super(props);
        this.state = {
            open: false
        };
    }

    static Transition(props) {
        return <Slide direction="up" {...props} />;
    }

    async componentDidMount() {
        this.props.fetchTeams();
    }

    handleClose() {
        this.setState({ open: false });
    };

    handleOpen(team) {
        this.setState({ open: true, selected: team });
    };

    render() {
        const { classes, teams, isLoading } = this.props;
        if (isLoading) return <Loader/>;
        return [
            <Typography key="title" variant="h4" gutterBottom>
                Teams
            </Typography>,
            <List key="teams-list">
                {teams.map((team, index) =>
                    <ListItem button key={team.name + index} onClick={() => this.handleOpen(team)}>
                        <ListItemAvatar>
                            <Avatar alt={team.name} src={team.avatar}/>
                        </ListItemAvatar>
                        <ListItemText primary={team.name} secondary={team.address}/>
                    </ListItem>
                )}
            </List>,
            <Dialog key="team-details-modal" fullScreen open={this.state.open} onClose={() => this.handleClose()}
                    TransitionComponent={Teams.Transition} className={classes.dialog}>
                <TeamDetail onClose={() => this.handleClose()} team={this.state.selected}/>
            </Dialog>
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
    fetchTeams: () => dispatch(TEAMS_FETCH)
});

const connectedTeams = connect(mapStateToProps, mapDispatchToProps)(Teams);
export default withStyles(styles)(connectedTeams);
