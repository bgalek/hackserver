import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api',
  state: {
    experimentGroups: []
  }
}).get({
  action: 'getExperimentGroups',
  property: 'experimentGroups',
  path: '/admin/experiments/groups',
  onSuccess: (state, payload) => {
    state.experimentGroups = payload.data.map(e => e.id)
  }
}).getStore()
