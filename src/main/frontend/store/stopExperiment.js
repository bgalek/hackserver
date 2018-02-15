import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'stopExperiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/stop`
}).getStore()
