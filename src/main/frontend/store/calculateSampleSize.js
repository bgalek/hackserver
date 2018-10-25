import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api'
}).get({
  action: 'calculateSampleSize',
  property: 'requiredSampleSize',
  queryParams: true,
  path: '/admin/experiments/calculate-sample-size',
  onError: (state) => {
    state.calculateSampleSize = { }
  }
}).getStore()
