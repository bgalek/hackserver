import React from "react";
import CssBaseline from '@material-ui/core/CssBaseline';
import { makeStyles } from "@material-ui/core";
import Header from "./Header";
import Notifications from "./Notifications";

const useStyles = makeStyles(theme => ({
    appBarSpacer: theme.mixins.toolbar,
    content: { flexGrow: 1, padding: theme.spacing(3), height: '100vh', overflow: 'auto' }
}));

function Layout({ children }) {
    const classes = useStyles();
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

Layout.propTypes = {};

export default Layout;