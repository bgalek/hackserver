import axios from "axios";
import { call, put, takeEvery } from 'redux-saga/effects'
import {
    CHALLENGES_FETCH_FAILED_TYPE,
    CHALLENGES_FETCH_SUCCEEDED_TYPE,
    CHALLENGES_FETCH_TYPE,
    SHOW_NOTIFICATION,
    TEAM_SEND_EXAMPLE_FAILED_TYPE,
    TEAM_SEND_EXAMPLE_SUCCEEDED_TYPE,
    TEAM_SEND_EXAMPLE_TYPE,
    TEAMS_FETCH_FAILED_TYPE,
    TEAMS_FETCH_SUCCEEDED_TYPE,
    TEAMS_FETCH_TYPE
} from "../actions";

const sagas = [
    function* rootSaga() {
        yield takeEvery(TEAMS_FETCH_TYPE, teamsFetchSaga);
        yield takeEvery(CHALLENGES_FETCH_TYPE, challengesFetchSaga);
        yield takeEvery(TEAM_SEND_EXAMPLE_TYPE, teamSendExampleSaga);
    }
];

function* teamsFetchSaga() {
    try {
        const teams = yield call(api, `/registration`);
        yield put({ type: TEAMS_FETCH_SUCCEEDED_TYPE, payload: teams });
    } catch (e) {
        yield put({ type: TEAMS_FETCH_FAILED_TYPE, message: e.message });
    }
}

function* challengesFetchSaga() {
    try {
        const challenges = yield call(api, `/challenges`);
        yield put({ type: CHALLENGES_FETCH_SUCCEEDED_TYPE, payload: challenges });
    } catch (e) {
        yield put({ type: CHALLENGES_FETCH_FAILED_TYPE, message: e.message });
    }
}

function* teamSendExampleSaga({ action }) {
    try {
        yield put(SHOW_NOTIFICATION(`Testing team '${action.team}' with challenge '${action.challenge}'!`, 'info'));
        const payload = yield call(api, `/challenges/${action.challenge}/run-example?team-id=${action.team}`);
        if (payload.errorMessage) {
            yield put({ type: TEAM_SEND_EXAMPLE_FAILED_TYPE, message: payload.errorMessage });
            yield put(SHOW_NOTIFICATION(`Test failed!`, 'warning'));
            yield put(SHOW_NOTIFICATION(payload.errorMessage, 'error'));
        } else {
            yield put({ type: TEAM_SEND_EXAMPLE_SUCCEEDED_TYPE, payload });
            yield put(SHOW_NOTIFICATION(`Test successful! Score: ${payload.score}`, 'success'));
        }
    } catch (e) {
        yield put({ type: TEAM_SEND_EXAMPLE_FAILED_TYPE, message: e.message });
    }
}

function api(path) {
    return axios.get(path).then(it => it.data);
}

export default sagas;