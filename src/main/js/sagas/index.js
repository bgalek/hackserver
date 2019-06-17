import axios from "axios";
import { call, put, takeEvery } from 'redux-saga/effects'
import {
    CHALLENGES_FETCH_FAILED_TYPE,
    CHALLENGES_FETCH_SUCCEEDED_TYPE,
    CHALLENGES_FETCH_TYPE,
    CHALLENGES_RESULTS_FETCH_FAILED_TYPE,
    CHALLENGES_RESULTS_FETCH_SUCCEEDED_TYPE,
    CHALLENGES_RESULTS_FETCH_TYPE,
    SCORES_FETCH_FAILED_TYPE,
    SCORES_FETCH_SUCCEEDED_TYPE,
    SCORES_FETCH_TYPE,
    SHOW_NOTIFICATION,
    TEAM_EXECUTE_CHALLENGE_FAILED_TYPE,
    TEAM_EXECUTE_CHALLENGE_SUCCEEDED_TYPE,
    TEAM_EXECUTE_CHALLENGE_TYPE,
    TEAM_SEND_EXAMPLE_FAILED_TYPE,
    TEAM_SEND_EXAMPLE_SUCCEEDED_TYPE,
    TEAM_SEND_EXAMPLE_TYPE,
    TEAMS_FETCH_FAILED_TYPE,
    TEAMS_FETCH_SUCCEEDED_TYPE,
    TEAMS_FETCH_TYPE,
} from "../actions";

const sagas = [
    function* rootSaga() {
        yield takeEvery(TEAMS_FETCH_TYPE, teamsFetchSaga);
        yield takeEvery(CHALLENGES_FETCH_TYPE, challengesFetchSaga);
        yield takeEvery(TEAM_SEND_EXAMPLE_TYPE, teamSendExampleSaga);
        yield takeEvery(TEAM_EXECUTE_CHALLENGE_TYPE, teamExecuteChallengeSaga);
        yield takeEvery(CHALLENGES_RESULTS_FETCH_TYPE, challengesLogFetchSaga);
        yield takeEvery(SCORES_FETCH_TYPE, scoresFetchSaga);
    }
];

function* teamsFetchSaga() {
    try {
        const teams = yield call(get, `/registration`);
        yield put({ type: TEAMS_FETCH_SUCCEEDED_TYPE, payload: teams });
    } catch (e) {
        yield put({ type: TEAMS_FETCH_FAILED_TYPE, message: e.message });
    }
}

function* challengesFetchSaga() {
    try {
        const challenges = yield call(get, `/challenges`);
        yield put({ type: CHALLENGES_FETCH_SUCCEEDED_TYPE, payload: challenges });
    } catch (e) {
        yield put({ type: CHALLENGES_FETCH_FAILED_TYPE, message: e.message });
    }
}

function* challengesLogFetchSaga({ team }) {
    try {
        const results = yield call(get, `/challenges/results?team-id=${team}`);
        yield put({ type: CHALLENGES_RESULTS_FETCH_SUCCEEDED_TYPE, payload: results });
    } catch (e) {
        yield put({ type: CHALLENGES_RESULTS_FETCH_FAILED_TYPE, message: e.message });
    }
}

function* scoresFetchSaga() {
    try {
        const scores = yield call(get, `/scores`);
        yield put({ type: SCORES_FETCH_SUCCEEDED_TYPE, payload: scores });
    } catch (e) {
        yield put({ type: SCORES_FETCH_FAILED_TYPE, message: e.message });
    }
}

function* teamSendExampleSaga({ team, challenge }) {
    try {
        yield put(SHOW_NOTIFICATION(`Testing team '${team}' with challenge '${challenge}'!`, 'info'));
        const payload = yield call(get, `/challenges/${challenge}/run-example?team-id=${team}`);
        if (payload.errorMessage) {
            yield put({ type: TEAM_SEND_EXAMPLE_FAILED_TYPE, message: payload.errorMessage });
            yield put(SHOW_NOTIFICATION(`Test failed!`, 'warning', payload));
            yield put(SHOW_NOTIFICATION(payload.errorMessage + '. Did You enter valid secret?', 'error'));
        } else {
            yield put({ type: TEAM_SEND_EXAMPLE_SUCCEEDED_TYPE, payload });
            yield put(SHOW_NOTIFICATION(`Test successful! Score: ${payload.score}`, 'success', payload));
        }
    } catch (e) {
        yield put({ type: TEAM_SEND_EXAMPLE_FAILED_TYPE, message: e.message });
    }
}

function* teamExecuteChallengeSaga({ team, challenge, secret }) {
    try {
        yield put(SHOW_NOTIFICATION(`Executing challenge '${challenge}' for team '${team}'!`, 'info'));
        const payload = yield call(post, `/challenges/${challenge}/execute?team-id=${team}`, secret);
        if (payload.errorMessage) {
            yield put({ type: TEAM_EXECUTE_CHALLENGE_FAILED_TYPE });
            yield put(SHOW_NOTIFICATION(`Execution failed!`, 'warning'));
            yield put(SHOW_NOTIFICATION(payload.errorMessage, 'error'));

        } else {
            yield put({ type: TEAM_EXECUTE_CHALLENGE_SUCCEEDED_TYPE, payload });
            yield put(SHOW_NOTIFICATION(`Challenge executed`, 'success'));
        }
    } catch (e) {
        yield put({ type: TEAM_EXECUTE_CHALLENGE_FAILED_TYPE, message: e.message });
    }
}

function get(path) {
    return axios.get(path).then(it => it.data);
}

function post(path, secret) {
    return axios.post(path, null, { headers: { 'Authorization': `Bearer: ${secret}` } })
        .then(it => it.data)
        .catch(err => {
            return { errorMessage : err.message }
        })
}

export default sagas;