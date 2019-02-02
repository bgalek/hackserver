import axios from "axios";
import { call, put, takeEvery } from 'redux-saga/effects'
import {
    CHALLENGES_FETCH,
    CHALLENGES_FETCH_FAILED,
    CHALLENGES_FETCH_SUCCEEDED,
    TEAMS_FETCH,
    TEAMS_FETCH_FAILED,
    TEAMS_FETCH_SUCCEEDED
} from "../actions";

const sagas = [
    function* rootSaga() {
        yield takeEvery(TEAMS_FETCH.type, teamsFetchSaga);
        yield takeEvery(CHALLENGES_FETCH.type, challengesFetchSaga);
    }
];

function* teamsFetchSaga() {
    try {
        const teams = yield call(api, `/registration`);
        yield put(Object.assign(TEAMS_FETCH_SUCCEEDED, { payload: teams }));
    } catch (e) {
        yield put(Object.assign(TEAMS_FETCH_FAILED, { message: e.message }));
    }
}

function* challengesFetchSaga() {
    try {
        const challenges = yield call(api, `/challenges`);
        yield put(Object.assign(CHALLENGES_FETCH_SUCCEEDED, { payload: challenges }));
    } catch (e) {
        yield put(Object.assign(CHALLENGES_FETCH_FAILED, { message: e.message }));
    }
}

function api(path) {
    return axios.get(path).then(it => it.data);
}

export default sagas;