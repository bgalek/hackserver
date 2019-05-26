import React from "react";
import { makeStyles } from '@material-ui/core/styles/index';
import CircularProgress from '@material-ui/core/CircularProgress/index';
import red from '@material-ui/core/colors/red';
import Grid from "@material-ui/core/Grid/index";

const useStyles = makeStyles(theme => ({
    container: {
        height: 'calc(100% - 64px)'
    },
    progress: {
        margin: theme.spacing(4),
        color: red[500],
    },
}));

function Loader() {
    const classes = useStyles();
    return <Grid className={classes.container} container justify="center" alignItems="center">
        <CircularProgress size={150} className={classes.progress} thickness={3}/>
    </Grid>
}

Loader.propTypes = {};

export default Loader;