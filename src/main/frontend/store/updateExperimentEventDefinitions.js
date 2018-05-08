import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'updateExperimentEventDefinitions',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/update-event-definitions`
}).getStore()
