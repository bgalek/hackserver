import { combineReducers } from 'redux'
import { connectRouter } from 'connected-react-router'
import drawerReducer from "./drawer";
import teamsReducer from "./teams";
import challengesReducer from "./challenges";
import notificationsReducer from "./notifications";

export default (history) => combineReducers({
    router: connectRouter(history),
    drawer: drawerReducer,
    teams: teamsReducer,
    challenges: challengesReducer,
    notifications: notificationsReducer,
})