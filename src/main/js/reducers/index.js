import { combineReducers } from 'redux'
import { connectRouter } from 'connected-react-router'
import drawerReducer from "./drawer";
import teamsReducer from "./teams";
import challengesReducer from "./challenges";
import scoresReducer from "./scores";
import notificationsReducer from "./notifications";
import resultsReducer from "./results";

export default (history) => combineReducers({
    router: connectRouter(history),
    drawer: drawerReducer,
    teams: teamsReducer,
    challenges: challengesReducer,
    scores: scoresReducer,
    notifications: notificationsReducer,
    results: resultsReducer
})