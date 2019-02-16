import { DRAWER_CLOSE, DRAWER_OPEN } from "../actions";

const defaultState = { open: false };

export default (state = defaultState, action) => {
    switch (action.type) {
        case DRAWER_OPEN.type:
            return { open: true };
        case DRAWER_CLOSE.type:
            return { open: false };
        default:
            return state;
    }
}