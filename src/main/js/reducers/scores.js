import { SCORES_FETCH_SUCCEEDED_TYPE, SCORES_UPDATED_TYPE } from "../actions";

const defaultState = {
    data: { updatedAt: new Date(0).toISOString(), scores: [] },
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case SCORES_FETCH_SUCCEEDED_TYPE:
            return { data: action.payload, isLoading: false };
        case SCORES_UPDATED_TYPE:
            return { data: action.payload };
        default:
            return state;
    }
}