import axios from 'axios'
import ExperimentDefinitionModel from '../model/experiment/experimentDefinition'
import ExperimentStatisticsModel from '../model/experiment/experimentStatistics'
import ExperimentBayesianStatisticsModel from '../model/experiment/experimentBayesianStatistics'

export default {
  state: {
    experimentDefinition: null,
    experimentStatistics: null,
    experimentBayesianHistograms: null,
    experimentReady: false,
    experimentError: null
  },

  actions: {
    getExperiment (context, experimentId) {
      context.dispatch('clearExperiment')
      const experimentDefinitionPromise = axios.get(`/api/admin/experiments/${experimentId}`)
      const experimentStatisticsPromise = axios.get(`/api/admin/statistics/${experimentId}`)
      const experimentBayesianHistogramsPromise = axios.get(`/api/bayes/histograms/${experimentId}`)

      return axios.all([experimentDefinitionPromise, experimentStatisticsPromise, experimentBayesianHistogramsPromise])
        .then((response) => {
          const [experimentDefinition, experimentStatistics, experimentBayesianHistograms] = response.map(it => it.data)
          context.commit('setFullExperiment', {experimentDefinition, experimentStatistics, experimentBayesianHistograms})
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
    setFullExperiment (state, {experimentDefinition, experimentStatistics, experimentBayesianHistograms}) {
      state.experimentDefinition = new ExperimentDefinitionModel(experimentDefinition)
      state.experimentStatistics = new ExperimentStatisticsModel(experimentStatistics)
      state.experimentBayesianHistograms = new ExperimentBayesianStatisticsModel(experimentBayesianHistograms)
      state.experimentReady = true
    },

    setExperimentReady (state, isReady) {
      state.experimentReady = isReady
    },

    setExperimentError (state, message) {
      state.experimentError = message
    }
  }
}