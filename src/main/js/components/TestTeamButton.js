import Button from "@material-ui/core/Button";
import React, { Component } from "react";
import ComputerIcon from "@material-ui/icons/Computer";
import { CHALLENGES_FETCH, TEAM_SEND_EXAMPLE } from "../actions";
import { connect } from "react-redux";
import { withStyles } from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";

const styles = theme => ({
    actionButton: {
        flex: '0 1 auto',
    },
    actionButtonIcon: {
        marginRight: theme.spacing.unit,
    }
});

class TestTeamButton extends Component {
    state = {
        open: false,
        challenge: ''
    };

    async componentDidMount() {
        this.props.fetchChallenges();
    }

    handleClickOpen = () => {
        this.setState({ open: true });
    };

    handleClose = () => {
        this.setState({ open: false });
        this.props.sendExample(this.props.team, this.state.challenge);
    };

    handleChange = event => {
        this.setState({ [event.target.name]: event.target.value });
    };

    render() {
        const { isLoading, challenges, classes } = this.props;

        if (isLoading) return <Button key="action" disabled className={classes.actionButton} variant="contained"
                                      color="secondary">
            <ComputerIcon className={classes.actionButtonIcon}/>
            Test me
        </Button>;
        return [
            <Button key="action" onClick={this.handleClickOpen}
                    className={classes.actionButton} variant="contained" color="secondary">
                <ComputerIcon className={classes.actionButtonIcon}/>
                Test me
            </Button>,
            <Dialog key="dialog" open={this.state.open} onClose={this.handleClose} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Select Challenge</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        To check your team, please select challenge you want to be checked with.
                    </DialogContentText>
                    <br/>
                    <Select name="challenge" displayEmpty value={this.state.challenge} onChange={this.handleChange}>
                        {challenges.map(challenge => <MenuItem key={challenge.id}
                                                               value={challenge.id}>{challenge.name}</MenuItem>)}
                    </Select>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button variant="contained" onClick={this.handleClose} color="primary">
                        Start
                    </Button>
                </DialogActions>
            </Dialog>];
    }
}

const mapStateToProps = (state) => ({
    challenges: state.challenges.data,
    isLoading: state.challenges.isLoading
});


const mapDispatchToProps = (dispatch) => ({
    fetchChallenges: () => dispatch(CHALLENGES_FETCH),
    sendExample: (...props) => dispatch(TEAM_SEND_EXAMPLE(...props))
});

export default connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(TestTeamButton));