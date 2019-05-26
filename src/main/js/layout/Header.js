import React from "react";
import PropTypes from "prop-types";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import MenuIcon from "@material-ui/icons/Menu";
import MainDrawer from "./MainDrawer";
import { DRAWER_OPEN } from "../actions";
import { connect } from "react-redux";
import IconButton from "@material-ui/core/IconButton";
import { makeStyles } from "@material-ui/core";

const useStyles = makeStyles(theme => ({
    appBar: { zIndex: theme.zIndex.drawer + 1 },
    menuButton: { marginRight: 20, [theme.breakpoints.up('sm')]: { display: 'none' } },
}));

function Header({ openDrawer }) {
    const classes = useStyles();
    return [
        <AppBar key="appbar" position="fixed" className={classes.appBar} elevation={2}>
            <Toolbar>
                <IconButton color="inherit" onClick={openDrawer} className={classes.menuButton}>
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6" color="inherit" noWrap>
                    Hackserver
                </Typography>
            </Toolbar>
        </AppBar>,
        <MainDrawer key="drawer"/>
    ];
}

Header.propTypes = {
    openDrawer: PropTypes.func.isRequired,
};

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch) => ({
    openDrawer: () => dispatch(DRAWER_OPEN)
});

export default connect(mapStateToProps, mapDispatchToProps)(Header);