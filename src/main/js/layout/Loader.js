import React from "react";
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';
import red from '@material-ui/core/colors/red';
import Grid from "@material-ui/core/Grid";

const styles = theme => ({
    container: {
        height: 'calc(100% - 64px)'
    },
    progress: {
        margin: theme.spacing.unit * 4,
        color: red[500],
    },
});

function Loader({ classes }) {
    return <Grid className={classes.container} container justify="center" alignItems="center">
        <CircularProgress size={150} className={classes.progress} thickness={3}/>
    </Grid>
}

Loader.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Loader);