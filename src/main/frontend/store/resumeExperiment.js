import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'resumeExperiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/resume`
}).getStore()
