import React from "react";
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Drawer from "@material-ui/core/Drawer";
import Divider from "@material-ui/core/Divider";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import PeopleIcon from '@material-ui/icons/People';
import CodeIcon from '@material-ui/icons/Code';
import FlightTakeoffIcon from '@material-ui/icons/FlightTakeoff';
import Hidden from "@material-ui/core/Hidden";
import { connect } from "react-redux";
import { push } from 'connected-react-router'
import { toggleDrawer } from "../actions";

const drawerWidth = 240;

const styles = theme => ({
    drawer: { width: drawerWidth, flexShrink: 0 },
    drawerPaper: { width: drawerWidth },
    content: { flexGrow: 1, padding: theme.spacing.unit * 3 },
    toolbar: theme.mixins.toolbar,
});

const MainDrawer = ({ classes, open, toggleDrawer, navigateTo }) => {
    const drawer = [
        <Hidden key="desktop-drawer" xsDown implementation="css">
            <div className={classes.toolbar}/>
        </Hidden>,
        <List key="main-menu">
            <ListItem button onClick={() => navigateTo('/')}>
                <ListItemIcon>
                    <PeopleIcon/>
                </ListItemIcon>
                <ListItemText primary="Teams"/>
            </ListItem>
            <ListItem button onClick={() => navigateTo('/challenges')}>
                <ListItemIcon>
                    <FlightTakeoffIcon/>
                </ListItemIcon>
                <ListItemText primary="Challenges"/>
            </ListItem>
        </List>,
        <Divider key="divider"/>,
        <List key="additional-menu">
            <ListItem button onClick={() => navigateTo('/about')}>
                <ListItemIcon>
                    <CodeIcon/>
                </ListItemIcon>
                <ListItemText primary="About"/>
            </ListItem>
        </List>,
    ];

    return [
        <Hidden key="mobile-drawer" smUp implementation="css">
            <Drawer variant="temporary" open={open} onClose={toggleDrawer} classes={{ paper: classes.drawerPaper }}>
                {drawer}
            </Drawer>
        </Hidden>,
        <Hidden key="desktop-drawer" xsDown implementation="css">
            <Drawer className={classes.drawer} variant="permanent" classes={{ paper: classes.drawerPaper }}>
                {drawer}
            </Drawer>
        </Hidden>
    ]
};

MainDrawer.propTypes = {
    classes: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
    open: state.drawer.open
});

const mapDispatchToProps = (dispatch) => ({
    toggleDrawer: () => dispatch(toggleDrawer),
    navigateTo: (route) => dispatch(push(route))
});

const connectedMainDrawer = connect(mapStateToProps, mapDispatchToProps)(MainDrawer);
export default withStyles(styles)(connectedMainDrawer);
