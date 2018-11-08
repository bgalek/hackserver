import axios from 'axios'
import ExperimentDefinitionModel from '../model/experiment/experimentDefinition'
import ExperimentStatisticsModel from '../model/experiment/experimentStatistics'
import ExperimentBayesianStatisticsModel from '../model/experiment/experimentBayesianStatistics'
import PENDING_STATE from './pendingState'
import ERROR_STATE from './errorState'

export default {
  state: {
    experimentDefinition: PENDING_STATE,
    experimentStatistics: PENDING_STATE,
    experimentBayesianHistograms: PENDING_STATE,
    experimentBayesianEqualizers: PENDING_STATE
  },

  actions: {
    getExperiment (context, experimentId) {
      const experimentDefinitionPromise = axios.get(`/api/admin/experiments/${experimentId}`)
      const experimentStatisticsPromise = axios.get(`/api/admin/statistics/${experimentId}`)
      const experimentBayesianHistogramsPromise = axios.get(`/api/bayes/histograms/${experimentId}`)
      const experimentBayesianEqualizersPromise = axios.get(`/api/bayes/verticalEqualizer/${experimentId}`)

      return axios.all([experimentDefinitionPromise, experimentStatisticsPromise, experimentBayesianHistogramsPromise, experimentBayesianEqualizersPromise])
        .then((response) => {
          const [experimentDefinition, experimentStatistics, experimentBayesianHistograms, experimentBayesianEqualizers] = response.map(it => it.data)
          context.commit('setExperimentDefinition', experimentDefinition)
          context.commit('setExperimentStatistics', experimentStatistics)
          context.commit('setExperimentBayesianHistograms', experimentBayesianHistograms)
          context.commit('setExperimentBayesianEqualizers', experimentBayesianEqualizers)
          context.dispatch('enrichedGoal/update')
        }).catch((response) => {
          context.commit('experimentStatisticsError')
          context.commit('experimentDefinitionError')
          context.commit('experimentBayesianHistogramsError')
          context.commit('experimentBayesianEqualizersError')
        })
    }
  },

  mutations: {
    setExperimentDefinition (state, experimentDefinition) {
      state.experimentDefinition = new ExperimentDefinitionModel(experimentDefinition)
    },

    setExperimentStatistics (state, experimentStatistics) {
      state.experimentStatistics = new ExperimentStatisticsModel(experimentStatistics)
    },

    setExperimentBayesianHistograms (state, experimentBayesianHistograms) {
      state.experimentBayesianHistograms = new ExperimentBayesianStatisticsModel(experimentBayesianHistograms)
    },

    setExperimentBayesianEqualizers (state, experimentBayesianEqualizers) {
      state.experimentBayesianEqualizers = new ExperimentBayesianStatisticsModel(experimentBayesianEqualizers)
    },

    experimentStatisticsError (state) {
      state.experimentStatistics = ERROR_STATE
    },

    experimentDefinitionError (state) {
      state.experimentDefinition = ERROR_STATE
    },

    experimentBayesianHistogramsError (state) {
      state.experimentBayesianHistograms = ERROR_STATE
    },

    experimentBayesianEqualizersError (state) {
      state.experimentBayesianEqualizers = ERROR_STATE
    }
  }
}
