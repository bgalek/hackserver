import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).post({
  action: 'createExperiment',
  path: '/admin/experiments'
}).getStore()
