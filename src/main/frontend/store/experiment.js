import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api',
  state: {
    experiment: { }
  }
}).get({
  action: 'getExperiment',
  property: 'experiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}`
}).getStore()
