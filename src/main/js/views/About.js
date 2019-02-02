import React from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';

const styles = theme => ({});

function About({ classes }) {
    return [
        <Typography key="title" variant="h4" gutterBottom>
            About
        </Typography>,
        <Typography key="about-text" variant="body1">
            Something about the initiative.
        </Typography>
    ];
}

About.defaultProps = { teams: [] };

About.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(About);
