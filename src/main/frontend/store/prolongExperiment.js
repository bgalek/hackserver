import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'prolongExperiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/prolong`
}).getStore()
