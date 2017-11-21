import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api',
  state: {
    experiments: []
  }
}).get({
  action: 'getExperiments',
  property: 'experiments',
  path: '/experiments/v1'
}).getStore()
