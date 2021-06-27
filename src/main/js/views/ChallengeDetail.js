import React from 'react';
import Typography from '@material-ui/core/Typography';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import { connect } from "react-redux";
import { bindActionCreators } from "redux";
import { SHOW_NOTIFICATION } from "../actions";
import Grid from "@material-ui/core/Grid";
import { formatDistanceToNow } from 'date-fns'
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import { makeStyles } from '@material-ui/core/styles';
import StarBorder from '@material-ui/icons/StarBorder';
import ListItemIcon from "@material-ui/core/ListItemIcon";
import Paper from "@material-ui/core/Paper";
import TestTeamButton from '../components/TestTeamButton';
import ExecuteChallengeButton from '../components/ExecuteChallengeButton';

const useStyles = makeStyles(theme => ({
    appBar: {
        position: 'relative',
    },
    description: {
        maxWidth: '100%',
        padding: theme.spacing(3, 2)
    },
    content: {
        padding: theme.spacing(4)
    },
    challengeName: {
        flex: '1 0 auto',
        marginLeft: theme.spacing(2),
        display: 'flex',
        alignItems: 'baseline'
    },
    nested: {
        paddingLeft: theme.spacing(4),
    }
}));

export function ChallengeDetail({ challenge, onClose }) {
    const classes = useStyles();
    const url = new URL(challenge.challengeEndpoint, 'http://localhost:8080');
    Object.entries(challenge.example.parameters).forEach(parameter => {
        parameter[1].forEach(parameterValue => {
            url.searchParams.append(parameter[0], parameterValue)
        });
    });
    return [
        <AppBar key="modal-title" className={classes.appBar}>
            <Toolbar>
                <IconButton color="inherit" onClick={() => onClose()} aria-label="Close">
                    <CloseIcon/>
                </IconButton>
                <Typography variant="h6" color="inherit" className={classes.challengeName}>
                    {challenge.name}&nbsp;
                    <Typography variant="caption" color="inherit">
                        <strong>started {formatDistanceToNow(new Date(challenge.activatedAt), { addSuffix: true })}</strong>
                    </Typography>
                </Typography>
                <TestTeamButton challenge={challenge.id} />
                <ExecuteChallengeButton challenge={challenge.id}/>
            </Toolbar>
        </AppBar>,
        <Grid key="modal-content" className={classes.content} direction="column" container>
            <Grid xs={12}>
                <Paper className={classes.description}>
                    {challenge.description.map((it, i) => <Typography key={`${challenge.name}-${i}`} variant="body1" gutterBottom>{it}</Typography>)}
                </Paper>
            </Grid>
            <Grid xs={12}>
                <List className={classes.root}>
                        <ListItem>
                            <ListItemText primary="Max points:" secondary={challenge.maxPoints}/>
                        </ListItem>
                        <ListItem>
                            <ListItemText primary="Endpoint:" secondary={challenge.challengeEndpoint}/>
                        </ListItem>
                        <ListItem>
                            <ListItemText primary="Parameters:"/>
                        </ListItem>
                        <List disablePadding>
                            {challenge.challengeParameters
                                .map(param => <ListItem key={param.name} className={classes.nested}>
                                    <ListItemIcon>
                                        <StarBorder/>
                                    </ListItemIcon>
                                    <ListItemText primary={param.name} secondary={param.desc}/>
                                </ListItem>)}
                        </List>
                        <ListItem>
                            <ListItemText primary="Response:" secondary={challenge.challengeResponse.type}/>
                        </ListItem>
                        <ListItem>
                            <ListItemText primary="Example Request:" secondary={decodeURIComponent(url.toString())}
                                          secondaryTypographyProps={{ style: { wordBreak: "break-all" } }}
                            />
                        </ListItem>
                        <ListItem>
                            <ListItemText primary="Example Response:"
                                          secondary={JSON.stringify(challenge.example.expectedSolution)}/>
                        </ListItem>
                    </List>
            </Grid>
        </Grid>
    ];
}

const mapDispatchToProps = dispatch => bindActionCreators({ showSnackbar: SHOW_NOTIFICATION }, dispatch);
export default connect(null, mapDispatchToProps)(ChallengeDetail);
