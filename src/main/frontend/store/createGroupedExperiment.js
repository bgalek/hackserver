import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).post({
  action: 'createGroupedExperiment',
  path: '/admin/experiments/create-paired-experiment'
}).getStore()
