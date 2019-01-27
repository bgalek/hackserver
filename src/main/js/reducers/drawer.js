const state = { open: false };

export default (state = state, action) => {
    switch (action.type) {
        case 'DRAWER_TOGGLE':
            const open = !state.open;
            return { open };
        default:
            return { open: false };
    }
}