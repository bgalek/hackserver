import Vapi from 'vuex-rest-api'
import apiError from './apiError'

export default new Vapi({
  baseURL: '/api',
  state: {
    bayesianStatistics: {}
  }
}).get({
  action: 'getBayesianStatistics',
  property: 'bayesianStatistics',
  path: ({experimentId}) => `/bayes/statistics/${experimentId}`,
  queryParams: true,
  onError: (state, error) => {
    console.log('getBayesianStatistics', 'onError', error)
    state.error = { bayesianStatistics: apiError(error) }
    state.bayesianStatistics = { }
  }
}).getStore()
