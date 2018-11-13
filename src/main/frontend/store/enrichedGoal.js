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
      const experiment = context.rootState.experimentStore.experimentDefinition
      const experimentStatistics = context.rootState.experimentStore.experimentStatistics.getForDevice(experiment.getBaseDeviceClass())
      const experimentGoal = experiment && experiment.goal
      let leadingDevice = experiment.getBaseDeviceClass()
      leadingDevice = leadingDevice.startsWith('phone') ? 'smartphone' : leadingDevice

      if (experimentGoal && leadingDevice && experimentStatistics && experimentStatistics.metrics && experimentStatistics.device === leadingDevice) {
        const leadingStats = experimentStatistics.metrics.filter(it => it.key === experimentGoal.leadingMetric)[0]

        if (leadingStats) {
          context.commit('update', new EnrichedGoal({
            requiredSampleSize: experimentGoal.requiredSampleSize,
            leadingStatsBaseCount: leadingStats.variants.filter(it => it.variant === 'base')[0].count
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
