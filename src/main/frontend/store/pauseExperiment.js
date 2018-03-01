import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'pauseExperiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/pause`
}).getStore()
