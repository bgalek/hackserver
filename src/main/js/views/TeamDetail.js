import React from 'react';
import Typography from '@material-ui/core/Typography';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import Grid from "@material-ui/core/Grid";
import Avatar from "@material-ui/core/Avatar";
import { makeStyles } from "@material-ui/core";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import TestTeamButton from "../components/TestTeamButton";
import { connect } from "react-redux";
import { bindActionCreators } from "redux";
import { SHOW_NOTIFICATION } from "../actions";
import ChallengeResults from "../components/ChallengeResults";
import TeamHealthIndicator from "../components/TeamHealthIndicator";

const useStyles = makeStyles(theme => ({
    appBar: {
        position: 'relative',
    },
    tableInfo: {
        margin: theme.spacing(2),
        flex: 1
    },
    header: {
        backgroundColor: '#eee',
        padding: theme.spacing(2)
    },
    card: {
        padding: theme.spacing(4),
    },
    avatar: {
        margin: theme.spacing(2),
        width: 128,
        height: 128,
    },
    challengeName: {
        flex: '1 0 auto',
        marginLeft: theme.spacing(2)
    }
}));

export function TeamDetail({ team, onClose }) {
    const classes = useStyles();
    return [
        <AppBar key="modal-title" className={classes.appBar}>
            <Toolbar>
                <IconButton color="inherit" onClick={() => onClose()} aria-label="Close">
                    <CloseIcon/>
                </IconButton>
                <Typography variant="h6" color="inherit" className={classes.challengeName}>
                    {team.name}
                </Typography>
                <TestTeamButton team={team}/>
            </Toolbar>
        </AppBar>,
        <Grid key="modal-content" container justify="center" alignItems="center">
            <Grid container justify="center" alignItems="center" className={classes.header}>
                <Avatar alt={team.name} src={team.avatar} className={classes.avatar}/>
                <Table className={classes.tableInfo}>
                    <TableHead>
                        <TableRow>
                            <TableCell>IP</TableCell>
                            <TableCell>PORT</TableCell>
                            <TableCell>STATUS</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        <TableRow>
                            <TableCell>{team.address}</TableCell>
                            <TableCell>{team.port}</TableCell>
                            <TableCell>
                                <TeamHealthIndicator health={team.health}/>
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </Grid>
            <ChallengeResults team={team.name}/>
        </Grid>
    ];
}

const mapDispatchToProps = dispatch => bindActionCreators({ showSnackbar: SHOW_NOTIFICATION }, dispatch);
export default connect(null, mapDispatchToProps)(TeamDetail);
