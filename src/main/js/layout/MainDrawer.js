import React from "react";
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Drawer from "@material-ui/core/Drawer";
import Divider from "@material-ui/core/Divider";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import Hidden from "@material-ui/core/Hidden";
import { connect } from "react-redux";
import { push } from 'connected-react-router'
import { DRAWER_CLOSE, DRAWER_OPEN } from "../actions";
import routes, { ADDITIONAL_MENU, MAIN_MENU } from "../routes";

const drawerWidth = 240;

const styles = theme => ({
    drawer: { width: drawerWidth, flexShrink: 0 },
    drawerPaper: { width: drawerWidth },
    content: { flexGrow: 1, padding: theme.spacing.unit * 3 },
    toolbar: theme.mixins.toolbar,
});

function MainDrawer({ classes, open, closeDrawer, openDrawer, navigateTo }) {
    const mainMenu = routes.filter(route => route.menu === MAIN_MENU)
        .map(route => renderRoute(route));

    const additionalMenu = routes.filter(route => route.menu === ADDITIONAL_MENU)
        .map(route => renderRoute(route));

    const drawer = [
        <Hidden key="desktop-drawer" xsDown implementation="css">
            <div className={classes.toolbar}/>
        </Hidden>,
        <List key="main-menu">{mainMenu}</List>,
        <Divider key="divider"/>,
        <List key="additional-menu">{additionalMenu}</List>,
    ];

    return [
        <Hidden key="mobile-drawer" smUp implementation="css">
            <Drawer variant="temporary" open={open} onClose={closeDrawer} classes={{ paper: classes.drawerPaper }}>
                {drawer}
            </Drawer>
        </Hidden>,
        <Hidden key="desktop-drawer" xsDown implementation="css">
            <Drawer className={classes.drawer} variant="permanent" classes={{ paper: classes.drawerPaper }}>
                {drawer}
            </Drawer>
        </Hidden>
    ];

    function renderRoute(route) {
        return <ListItem key={route.path} button onClick={() => navigateTo(route.path)}>
            <ListItemIcon>{route.icon}</ListItemIcon>
            <ListItemText primary={route.label}/>
        </ListItem>;
    }
}

MainDrawer.propTypes = {
    classes: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
    open: state.drawer.open
});

const mapDispatchToProps = (dispatch) => ({
    closeDrawer: () => dispatch(DRAWER_CLOSE),
    openDrawer: () => dispatch(DRAWER_OPEN),
    navigateTo: (route) => {
        dispatch(push(route));
        dispatch(DRAWER_CLOSE);
    }
});

const connectedMainDrawer = connect(mapStateToProps, mapDispatchToProps)(MainDrawer);
export default withStyles(styles)(connectedMainDrawer);
