import { TEAM_UPDATED_TYPE, TEAMS_FETCH_SUCCEEDED_TYPE } from "../actions";

const defaultState = {
    data: [],
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case TEAMS_FETCH_SUCCEEDED_TYPE:
            return { data: action.payload, isLoading: false };
        case TEAM_UPDATED_TYPE:
            if (!state.data.find(it => it.name === action.payload.name)) return { data: state.data.concat([action.payload]) };
            return {
                data: state.data.map(it => {
                    return it.name === action.payload.name ? Object.assign(it, action.payload) : it;
                })
            };
        default:
            return state;
    }
}