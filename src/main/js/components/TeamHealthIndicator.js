import React from "react";
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import DoneIcon from "@material-ui/core/SvgIcon/SvgIcon";
import green from "@material-ui/core/colors/green";
import red from "@material-ui/core/colors/red";
import Chip from "@material-ui/core/Chip";
import ErrorIcon from "@material-ui/icons/Error";

const styles = theme => ({});

function TeamHealthIndicator({ health, classes }) {
    const icon = health ? <DoneIcon/> : <ErrorIcon/>;
    const color = health ? green.A100 : red.A100;
    const status = health ? 'HEALTHY' : 'DEAD';
    return <Chip style={{ backgroundColor: color }} label={status} icon={icon}/>;
}


TeamHealthIndicator.defaultProps = { teams: [] };

TeamHealthIndicator.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(TeamHealthIndicator);
