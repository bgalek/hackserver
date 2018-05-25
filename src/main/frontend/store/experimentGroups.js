import Vapi from 'vuex-rest-api'
import _ from 'lodash'

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
    state.experimentGroups = _.map(payload.data, (experimentGroupData) => experimentGroupData.id)
  }
}).getStore()
