import React from 'react';
import ReactDOM from 'react-dom';
import Layout from "./layout/Layout";
import { ConnectedRouter } from 'connected-react-router'
import { Route, Switch } from "react-router-dom";
import { Provider } from "react-redux";
import { createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles';
import configureStore from "./store";
import { createHashHistory } from "history";
import routes from './routes'
import { SnackbarProvider } from "notistack";

const history = createHashHistory();
const store = configureStore(history);
const theme = createMuiTheme({
    palette: { primary: { main: '#FF3D00' }, secondary: { main: '#37474F' } },
    typography: { useNextVariants: true }
});

const App = () => {
    return <MuiThemeProvider theme={theme}>
        <Provider store={store}>
            <ConnectedRouter history={history}>
                <SnackbarProvider>
                    <Layout>
                        <Switch>
                            <Route exact path="/" component={routes.find(route => route.home).view}/>
                            {routes.map(route => renderRoute(route))}
                        </Switch>
                    </Layout>
                </SnackbarProvider>
            </ConnectedRouter>
        </Provider>
    </MuiThemeProvider>
};

function renderRoute(route) {
    return <Route key={route.path} path={route.path} component={route.view}/>;
}

ReactDOM.render(<App/>, document.getElementById('app'));
