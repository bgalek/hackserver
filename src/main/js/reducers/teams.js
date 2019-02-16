import { TEAM_REGISTERED, TEAMS_FETCH_SUCCEEDED } from "../actions";

const defaultState = {
    data: [],
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case TEAMS_FETCH_SUCCEEDED.type:
            return { data: action.payload, isLoading: false };
        case TEAM_REGISTERED.type:
            return { data: state.data.concat([action.payload]) };
        default:
            return state;
    }
}