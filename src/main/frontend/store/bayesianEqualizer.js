import Vapi from 'vuex-rest-api'
import apiError from './apiError'

export default new Vapi({
  baseURL: '/api',
  state: {
    bayesianEqualizer: {}
  }
}).get({
  action: 'getBayesianEqualizer',
  property: 'bayesianEqualizer',
  path: ({experimentId}) => `/bayes/verticalEqualizer/${experimentId}`,
  queryParams: true,
  onError: (state, error) => {
    console.log('getBayesianEqualizer', 'onError', error)
    state.error = { bayesianEqualizer: apiError(error) }
    state.bayesianEqualizer = { }
  }
}).getStore()
