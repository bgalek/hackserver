import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'updateExperimentDescriptions',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/update-descriptions`
}).getStore()
