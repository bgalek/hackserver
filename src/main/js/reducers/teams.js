import { TEAM_REGISTERED_TYPE, TEAMS_FETCH_SUCCEEDED_TYPE } from "../actions";

const defaultState = {
    data: [],
    isLoading: true,
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case TEAMS_FETCH_SUCCEEDED_TYPE:
            return { data: action.payload, isLoading: false };
        case TEAM_REGISTERED_TYPE:
            return { data: state.data.concat([action.payload]) };
        default:
            return state;
    }
}