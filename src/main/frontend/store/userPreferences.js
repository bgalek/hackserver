export const userPreferences = {
  state: {
    filters: {
      myExperiments: true,
      statusFilter: 'all'
    }
  },
  mutations: {
    updateMyExperimentsFilter: (state, payload) => {
      state.filters.myExperiments = payload
    },
    updateStatusFilter: (state, payload) => {
      state.filters.statusFilter = payload
    }
  },
  actions: {
    updateMyExperimentsFilter: (context, payload) => {
      context.commit('updateMyExperimentsFilter', payload)
    },
    updateStatusFilter: (context, payload) => {
      context.commit('updateStatusFilter', payload)
    }
  }
}
