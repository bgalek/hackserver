import Vapi from 'vuex-rest-api'
import apiError from './apiError'

export default new Vapi({
  baseURL: '/api',
  state: {
    bayesianHistograms: {}
  }
}).get({
  action: 'getBayesianHistograms',
  property: 'bayesianHistograms',
  path: ({experimentId, device}) => `/bayes/histograms/${experimentId}/${device}`,
  queryParams: true,
  onError: (state, error) => {
    console.log('getBayesianHistograms', 'onError', error)
    state.error = { bayesianHistograms: apiError(error) }
    state.bayesianHistograms = { }
  }
}).getStore()
