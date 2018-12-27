import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'ungroupExperiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/remove-from-group`
}).getStore()
