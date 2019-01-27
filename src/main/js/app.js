import React from 'react';
import ReactDOM from 'react-dom';
import Layout from "./layout/Layout";
import { ConnectedRouter } from 'connected-react-router'
import { Route, Switch } from "react-router-dom";
import { Provider } from "react-redux";
import { createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles';
import configureStore from "./store";

import Teams from "./views/Teams";
import About from "./views/About";
import { createBrowserHistory } from "history";
import Challenges from "./views/Challenges";

const history = createBrowserHistory();
const store = configureStore(history);
const theme = createMuiTheme({
    palette: { primary: { main: '#FF3D00' }, secondary: { main: '#37474F' } },
    typography: { useNextVariants: true }
});

const App = () => {
    return (
        <MuiThemeProvider theme={theme}>
            <Provider store={store}>
                <ConnectedRouter history={history}>
                    <Layout>
                        <Switch>
                            <Route exact path="/about" component={About}/>
                            <Route path="/challenges" component={Challenges}/>
                            <Route path="/" component={Teams}/>
                        </Switch>
                    </Layout>
                </ConnectedRouter>
            </Provider>
        </MuiThemeProvider>
    )
};

ReactDOM.render(
    <App/>,
    document.getElementById('app')
);
