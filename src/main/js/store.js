import React from 'react';
import { applyMiddleware, compose, createStore } from 'redux'
import { routerMiddleware } from 'connected-react-router'
import rootReducer from "./reducers";

export default function configureStore(history) {
    return createStore(
        rootReducer(history),
        compose(applyMiddleware(routerMiddleware(history))),
    )
}