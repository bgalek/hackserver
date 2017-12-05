import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api',
  state: {
    experimentStatistics: {}
  }
}).get({
  action: 'getExperimentStatistics',
  property: 'experimentStatistics',
  path: ({experimentId}) => `/statistics/${experimentId}`,
  queryParams: true
}).getStore()
