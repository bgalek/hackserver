import EnrichedGoal from '../model/experiment/enriched-goal'

export default {
  namespaced: true,
  state: {
    enrichedGoal: {}
  },
  getters: {
    enrichedGoal: state => {
      return state.enrichedGoal
    }
  },
  actions: {
    update (context) {
      const experiment = context.rootState.experiment.experiment
      const experimentStatistics = context.rootState.experimentStatistics.experimentStatistics
      const experimentGoal = experiment && experiment.goal
      const leadingDevice = experiment.getBaseDeviceClass()

      if (experimentGoal && leadingDevice && experimentStatistics && experimentStatistics.metrics &&
          experimentStatistics.device === leadingDevice) {
        const leadingStats = experimentStatistics.metrics[experimentGoal.leadingMetric]

        if (leadingStats) {
          context.commit('update', new EnrichedGoal({
            experimentGoal: experimentGoal,
            leadingStatsBaseCount: leadingStats.base.count
          }))
        }
      }
    }
  },
  mutations: {
    update  (state, enrichedGoal) {
      state.enrichedGoal = enrichedGoal
    }
  }
}
