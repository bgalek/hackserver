import axios from 'axios'
import ExperimentDefinitionModel from '../model/experiment/experimentDefinition'
import ExperimentStatisticsModel from '../model/experiment/experimentStatistics'
import ExperimentBayesianStatisticsModel from '../model/experiment/experimentBayesianStatistics'

export default {
  state: {
    experimentDefinition: null,
    experimentStatistics: null,
    experimentBayesianHistograms: null,
    experimentBayesianEqualizers: null,
    experimentReady: false,
    experimentError: null
  },

  actions: {
    getExperiment (context, experimentId) {
      context.dispatch('clearExperiment')
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
          context.commit('setExperimentReady', true)
        }).catch(response => {
          context.commit('setExperimentError', response.toString())
        })
    },

    clearExperiment (context) {
      context.commit('setExperimentReady', false)
      context.commit('setExperimentError', null)
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

    setExperimentReady (state, isReady) {
      state.experimentReady = isReady
    },

    setExperimentError (state, message) {
      state.experimentError = message
    }
  }
}
