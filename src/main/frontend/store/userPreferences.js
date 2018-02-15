export const userPreferences = {
  state: {
    filters: {
      myExperiments: true
    }
  },
  mutations: {
    updateMyExperimentsFilter: (state, payload) => {
      state.filters.myExperiments = payload
    }
  },
  actions: {
    updateMyExperimentsFilter: (context, payload) => {
      context.commit('updateMyExperimentsFilter', payload)
    }
  }
}
