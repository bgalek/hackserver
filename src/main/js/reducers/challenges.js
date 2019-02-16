import { CHALLENGES_FETCH_SUCCEEDED } from "../actions";

const defaultState = {
    data: [],
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case CHALLENGES_FETCH_SUCCEEDED.type:
            return { data: action.payload, isLoading: false };
        default:
            return state;
    }
}