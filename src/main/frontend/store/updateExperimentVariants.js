import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).put({
  action: 'updateExperimentVariants',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/update-variants`
}).getStore()
