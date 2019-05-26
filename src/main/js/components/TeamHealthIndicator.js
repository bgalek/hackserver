import React from "react";
import DoneIcon from "@material-ui/icons/Done";
import ErrorIcon from "@material-ui/icons/Error";
import green from "@material-ui/core/colors/green";
import red from "@material-ui/core/colors/red";
import Chip from "@material-ui/core/Chip";
import PropTypes from "prop-types";

function TeamHealthIndicator({ health }) {
    const icon = health ? <DoneIcon/> : <ErrorIcon/>;
    const color = health ? green.A100 : red.A100;
    const status = health ? 'HEALTHY' : 'DEAD';
    return <Chip style={{ backgroundColor: color }} label={status} icon={icon}/>;
}

TeamHealthIndicator.defaultProps = { teams: [] };
TeamHealthIndicator.propTypes = { health: PropTypes.bool.isRequired };

export default TeamHealthIndicator;
