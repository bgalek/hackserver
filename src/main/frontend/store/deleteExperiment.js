import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).delete({
  action: 'deleteExperiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}`
}).getStore()
