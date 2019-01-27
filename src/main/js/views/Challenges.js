import React from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';

const styles = theme => ({});

const Challenges = ({ classes }) => {
    return <Typography variant="h4" gutterBottom>Chellenges</Typography>;
};

Challenges.defaultProps = { teams: [] };

Challenges.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Challenges);
