import React, { Component } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { withSnackbar } from 'notistack';
import { HIDE_NOTIFICATION } from '../actions';
import Button from "@material-ui/core/Button";
import { Dialog } from "@material-ui/core";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import DialogTitle from "@material-ui/core/DialogTitle";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";

class Notifications extends Component {

    state = {
        open: false,
        details: {}
    };

    displayed = [];

    storeDisplayed = (id) => {
        this.displayed = [...this.displayed, id];
    };

    shouldComponentUpdate(nextProps, nextState, nextContext) {
        if (nextState.open !== this.state.open) return true;
        const { notifications: newSnacks = [] } = nextProps;
        const { notifications: currentSnacks } = this.props;
        let notExists = false;
        for (let i = 0; i < newSnacks.length; i += 1) {
            if (notExists) continue;
            notExists = notExists || !currentSnacks.filter(({ key }) => newSnacks[i].key === key).length;
        }
        return notExists;
    }

    componentDidUpdate() {
        const { notifications = [] } = this.props;
        notifications.forEach((notification) => {
            if (this.displayed.includes(notification.key)) return;
            if (notification.details) {
                this.props.enqueueSnackbar(notification.message, {
                    ...notification.options,
                    action: <Button onClick={() => this.showModal(notification.details)} size="small">Details</Button>
                })
            } else {
                this.props.enqueueSnackbar(notification.message, notification.options);
            }
            this.storeDisplayed(notification.key);
            this.props.removeSnackbar(notification.key);
        });
    }

    showModal = (details) => {
        this.setState({ details, open: true });
    };

    render() {
        return <Dialog fullWidth onClose={() => this.setState({ open: false })} open={this.state.open}>
            <DialogTitle id="alert-dialog-title">Notification Details</DialogTitle>
            <DialogContent>
                <List dense>{Object.entries(this.state.details)
                    .map((entry, i) =>
                        <ListItem key={`entry-${i}`}>
                            {entry[1] ? <ListItemText primary={entry[0]} secondary={entry[1]}/> : null}
                        </ListItem>
                    )}</List>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => this.setState({ open: false })} color="primary" autoFocus>
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    }
}

const mapStateToProps = store => ({ notifications: store.notifications.notifications, });
const mapDispatchToProps = dispatch => bindActionCreators({ removeSnackbar: HIDE_NOTIFICATION }, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(withSnackbar(Notifications));