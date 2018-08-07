import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'makeExperimentFullOn',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/full-on`
}).getStore()
