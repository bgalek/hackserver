import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'deleteExperiment',
  path: '/admin/experiments'
}).getStore()
