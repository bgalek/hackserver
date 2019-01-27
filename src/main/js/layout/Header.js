import React from "react";
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import MenuIcon from "@material-ui/icons/Menu";
import MainDrawer from "./MainDrawer";
import { toggleDrawer } from "../actions";
import { connect } from "react-redux";
import IconButton from "@material-ui/core/IconButton";

const styles = theme => ({
    appBar: { zIndex: theme.zIndex.drawer + 1 },
    menuButton: { marginRight: 20, [theme.breakpoints.up('sm')]: { display: 'none' } },
});

const Header = ({ classes, toggleDrawer }) => {
    return [
        <AppBar key="appbar" position="fixed" className={classes.appBar} elevation={2}>
            <Toolbar>
                <IconButton color="inherit" onClick={toggleDrawer} className={classes.menuButton}>
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6" color="inherit" noWrap>
                    Hackserver
                </Typography>
            </Toolbar>
        </AppBar>,
        <MainDrawer key="drawer"/>
    ];
};

Header.propTypes = {
    classes: PropTypes.object.isRequired,
};

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch) => ({
    toggleDrawer: () => dispatch(toggleDrawer)
});

const connectedHeader = connect(mapStateToProps, mapDispatchToProps)(Header);
export default withStyles(styles)(connectedHeader);