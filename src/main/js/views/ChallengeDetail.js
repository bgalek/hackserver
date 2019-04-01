import React from 'react';
import Typography from '@material-ui/core/Typography';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import {withStyles} from "@material-ui/core";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import {SHOW_NOTIFICATION} from "../actions";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";

const styles = theme => ({
    appBar: {
        position: 'relative',
    },
    card: {
        margin: 10,
        padding: 10
    },
    challengeName: {
        flex: '1 0 auto',
    }
});

export function ChallengeDetail({challenge, classes, onClose}) {
    return [
        <AppBar key="modal-title" className={classes.appBar}>
            <Toolbar>
                <IconButton color="inherit" onClick={() => onClose()} aria-label="Close">
                    <CloseIcon/>
                </IconButton>
                <Typography variant="h6" color="inherit" className={classes.challengeName}>
                    {challenge.name}
                </Typography>
            </Toolbar>
        </AppBar>,
        <Card key="modal-content" className={classes.card}>
            <CardContent>
                <Typography variant="h5" gutterBottom>{challenge.description}</Typography>
                <Typography>{challenge.activatedAt}</Typography>
                <Typography>{challenge.challengeEndpoint}</Typography>
                <Typography>{JSON.stringify(challenge.challengeParams)}</Typography>
                <Typography>{JSON.stringify(challenge.challengeResponse)}</Typography>
            </CardContent>
        </Card>
    ];
}

const mapDispatchToProps = dispatch => bindActionCreators({showSnackbar: SHOW_NOTIFICATION}, dispatch);
export default connect(null, mapDispatchToProps)(withStyles(styles)(ChallengeDetail));
