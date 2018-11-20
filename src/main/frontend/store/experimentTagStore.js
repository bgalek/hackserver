import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api',
  state: {
    experimentTags: []
  }
}).get({
  action: 'getAvailableExperimentTags',
  property: 'experimentTags',
  path: '/admin/experiments/tags',
  onSuccess: (state, payload) => {
    state.experimentTags = payload.data.map(t => t.id)
  }
}).getStore()
