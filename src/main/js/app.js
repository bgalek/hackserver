import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import HomePage from "./views/HomePage";

class App extends Component {

    render() {
        return (
            <HomePage/>
        )
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('app')
);
