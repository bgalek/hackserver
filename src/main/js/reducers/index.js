import { combineReducers } from 'redux'
import { connectRouter } from 'connected-react-router'
import drawer from "./drawer";

export default (history) => combineReducers({
    router: connectRouter(history),
    drawer
})