import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';
import {CHALLENGES_FETCH} from "../actions";
import {connect} from "react-redux";
import Loader from "./Teams";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import AccessAlarmsIcon from '@material-ui/icons/AccessAlarms';
import Dialog from "@material-ui/core/Dialog";
import Slide from "@material-ui/core/Slide";
import ChallengeDetail from "./ChallengeDetail";

const styles = theme => ({
    dialog: {
        backgroundColor: '#fafafa',
    }
});

class Challenges extends Component {

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
        this.props.fetchChallenges();
    }

    handleClose() {
        this.setState({open: false});
    };

    handleOpen(challenge) {
        this.setState({open: true, selected: challenge});
    };

    render() {
        const {classes, challenges, isLoading} = this.props;
        if (isLoading) return <Loader/>;
        return [
            <Typography key="title" variant="h4" gutterBottom>
                Challenges
            </Typography>,
            <List key="challenge-list">
                {challenges.map((challenge, index) =>
                    <ListItem button key={challenge.name + index} onClick={() => this.handleOpen(challenge)}>
                        <ListItemAvatar>
                            <Avatar>
                                <AccessAlarmsIcon/>
                            </Avatar>
                        </ListItemAvatar>
                        <ListItemText primary={challenge.name}/>
                    </ListItem>
                )}
            </List>,
            <Dialog key="challenge-details-modal" fullScreen open={this.state.open} onClose={() => this.handleClose()}
                    TransitionComponent={Challenges.Transition} className={classes.dialog}>
                <ChallengeDetail onClose={() => this.handleClose()} challenge={this.state.selected}/>
            </Dialog>
        ];
    }
}

Challenges.defaultProps = {teams: []};

Challenges.propTypes = {
    classes: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
    challenges: state.challenges.data,
    isLoading: state.teams.isLoading
});

const mapDispatchToProps = (dispatch) => ({
    fetchChallenges: () => dispatch(CHALLENGES_FETCH)
});

const connectedChallenges = connect(mapStateToProps, mapDispatchToProps)(Challenges);
export default withStyles(styles)(connectedChallenges);
