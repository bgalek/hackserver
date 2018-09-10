import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).post({
  action: 'addExperimentToGroup',
  path: '/admin/experiments/groups/'
}).getStore()
