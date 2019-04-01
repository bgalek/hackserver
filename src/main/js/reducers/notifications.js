import { HIDE_NOTIFICATION_TYPE, SHOW_NOTIFICATION_TYPE } from "../actions";

const defaultState = {
    notifications: [],
};

export default (state = defaultState, action) => {
    switch (action.type) {
        case SHOW_NOTIFICATION_TYPE:
            return { ...state, notifications: [...state.notifications, { ...action.notification }] };
        case HIDE_NOTIFICATION_TYPE:
            return {
                ...state, notifications: state.notifications.filter(notification => notification.key !== action.key),
            };
        default:
            return state;
    }
};
