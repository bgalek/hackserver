import { CHALLENGES_RESULTS_FETCH_SUCCEEDED_TYPE, CHALLENGES_RESULTS_UPDATED_TYPE } from "../actions";

const defaultState = {
    data: [],
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case CHALLENGES_RESULTS_FETCH_SUCCEEDED_TYPE:
            return { data: action.payload, isLoading: false };
        case CHALLENGES_RESULTS_UPDATED_TYPE:
            return { data: state.data.concat(action.payload), isLoading: false };
        default:
            return state;
    }
}