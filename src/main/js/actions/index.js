export const DRAWER_OPEN_TYPE = 'DRAWER_OPEN';
export const DRAWER_CLOSE_TYPE = 'DRAWER_CLOSE';
export const TEAMS_FETCH_TYPE = 'TEAMS_FETCH';
export const TEAMS_FETCH_SUCCEEDED_TYPE = 'TEAMS_FETCH_SUCCEEDED';
export const TEAMS_FETCH_FAILED_TYPE = 'TEAMS_FETCH_FAILED';
export const TEAM_REGISTERED_TYPE = 'TEAM_REGISTERED';
export const CHALLENGES_FETCH_TYPE = 'CHALLENGES_FETCH';
export const CHALLENGES_FETCH_SUCCEEDED_TYPE = 'CHALLENGES_FETCH_SUCCEEDED';
export const CHALLENGES_FETCH_FAILED_TYPE = 'CHALLENGES_FETCH_FAILED';
export const SHOW_NOTIFICATION_TYPE = 'ENQUEUE_SNACKBAR';
export const HIDE_NOTIFICATION_TYPE = 'REMOVE_SNACKBAR';
export const TEAM_SEND_EXAMPLE_TYPE = 'TEAM_SEND_EXAMPLE';
export const TEAM_SEND_EXAMPLE_SUCCEEDED_TYPE = 'TEAM_SEND_EXAMPLE_SUCCEEDED';
export const TEAM_SEND_EXAMPLE_FAILED_TYPE = 'TEAM_SEND_EXAMPLE_FAILED';

export const DRAWER_OPEN = { type: DRAWER_OPEN_TYPE };
export const DRAWER_CLOSE = { type: DRAWER_CLOSE_TYPE };
export const TEAMS_FETCH = { type: TEAMS_FETCH_TYPE };
export const TEAMS_FETCH_SUCCEEDED = { type: TEAMS_FETCH_SUCCEEDED_TYPE };
export const TEAMS_FETCH_FAILED = { type: TEAMS_FETCH_FAILED_TYPE };
export const CHALLENGES_FETCH = { type: CHALLENGES_FETCH_TYPE };
export const TEAM_REGISTERED = { type: TEAM_REGISTERED_TYPE };
export const CHALLENGES_FETCH_SUCCEEDED = { type: CHALLENGES_FETCH_SUCCEEDED_TYPE };
export const CHALLENGES_FETCH_FAILED = { type: CHALLENGES_FETCH_FAILED_TYPE };

export const TEAM_SEND_EXAMPLE = (team, challenge) => ({
    type: TEAM_SEND_EXAMPLE_TYPE,
    action: { team, challenge }
});

export const SHOW_NOTIFICATION = (message, type) => ({
    type: SHOW_NOTIFICATION_TYPE,
    notification: {
        key: new Date().getTime() + Math.random(),
        message,
        options: {
            variant: type,
        },
    },
});

export const HIDE_NOTIFICATION = key => ({
    type: HIDE_NOTIFICATION_TYPE,
    key
});