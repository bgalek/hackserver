import React from "react";
import CssBaseline from '@material-ui/core/CssBaseline';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Header from "./Header";
import Notifications from "./Notifications";

const styles = theme => ({
    appBarSpacer: theme.mixins.toolbar,
    content: { flexGrow: 1, padding: theme.spacing.unit * 3, height: '100vh', overflow: 'auto' }
});

function Layout({ classes, children }) {
    return [
        <CssBaseline key="cssBaseLine"/>,
        <Header key="header"/>,
        <main key="layout" className={classes.content}>
            <div className={classes.appBarSpacer}/>
            {children}
        </main>,
        <Notifications key="notifications"/>
    ]
}

Layout.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Layout);