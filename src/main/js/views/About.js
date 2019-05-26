import React from 'react';
import Typography from '@material-ui/core/Typography';

function About() {
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

About.propTypes = {};

export default About;
