import React from 'react';
import createSagaMiddleware from 'redux-saga'
import { applyMiddleware, compose, createStore } from 'redux'
import { routerMiddleware } from 'connected-react-router'
import rootReducer from "./reducers";
import sagas from "./sagas";

export default function configureStore(history) {
    const sagaMiddleware = createSagaMiddleware();
    const composeWithDevTools = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
    const store = createStore(
        rootReducer(history),
        composeWithDevTools(
            applyMiddleware(
                routerMiddleware(history),
                sagaMiddleware,
            )),
    );
    sagas.forEach((saga) => sagaMiddleware.run(saga));
    return store
}