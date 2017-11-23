import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api',
  state: {
    experiment: { }
  }
}).get({
  action: 'getExperiment',
  property: 'experiment',
  path: ({experimentId}) => `/experiments/${experimentId}/v1`
}).getStore()
