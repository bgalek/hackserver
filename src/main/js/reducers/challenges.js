import { CHALLENGES_FETCH_SUCCEEDED_TYPE } from "../actions";

const defaultState = {
    data: [],
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case CHALLENGES_FETCH_SUCCEEDED_TYPE:
            return { data: action.payload, isLoading: false };
        default:
            return state;
    }
}