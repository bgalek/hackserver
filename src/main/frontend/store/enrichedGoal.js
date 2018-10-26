export default {
  namespaced: true,
  state: {
    enrichedGoal: {aa:1}
  },
  getters: {
    enrichedGoal: state => {
      return state.enrichedGoal;
    }
  },
  mutations: {
    update(state, command) {
      const goal = command.goal
      const experimentStatistics = command.experimentStatistics
      const leadingDevice = command.leadingDevice

      if (goal) {
        const leadingStats = experimentStatistics.metrics.find(m => m.key === goal.leadingMetric)

        if (leadingStats) {
          state.enrichedGoal = {
            leadingDevice: leadingDevice,
            goal: goal,
            leadingStatsBase: leadingStats.variants.find(r => r.variant === 'base')
          }
        }
      };
    }
  }
}
