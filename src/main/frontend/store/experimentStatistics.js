import Vapi from 'vuex-rest-api'
import apiError from './apiError'

export default new Vapi({
  baseURL: '/api',
  state: {
    experimentStatistics: {}
  }
}).get({
  action: 'getExperimentStatistics',
  property: 'experimentStatistics',
  path: ({experimentId}) => `/statistics/${experimentId}`,
  queryParams: true,
  onError: (state, error) => {
    state.error = { experimentStatistics: apiError(error) }
  }
}).getStore()
