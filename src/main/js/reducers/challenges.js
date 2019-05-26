import { CHALLENGES_FETCH_SUCCEEDED_TYPE, CHALLENGES_UPDATED_TYPE } from "../actions";

const defaultState = {
    data: [],
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case CHALLENGES_FETCH_SUCCEEDED_TYPE:
            return { data: action.payload, isLoading: false };
        case CHALLENGES_UPDATED_TYPE:
            if (!state.data.find(it => it.id === action.payload.id)) {
                return { data: state.data.concat([action.payload]) }
            }
            return {
                data: state.data.map(it => {
                    return it.id === action.payload.id ? Object.assign(it, action.payload) : it;
                })
            };
        default:
            return state;
    }
}