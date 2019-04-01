import { DRAWER_CLOSE_TYPE, DRAWER_OPEN_TYPE } from "../actions";

const defaultState = { open: false };

export default (state = defaultState, action) => {
    switch (action.type) {
        case DRAWER_OPEN_TYPE:
            return { open: true };
        case DRAWER_CLOSE_TYPE:
            return { open: false };
        default:
            return state;
    }
}