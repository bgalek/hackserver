import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'startExperiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/start`
}).getStore()
