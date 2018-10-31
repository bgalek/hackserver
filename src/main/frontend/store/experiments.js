import Vapi from 'vuex-rest-api'
import _ from 'lodash'
import ExperimentDefinitionModel from '../model/experiment/experiment'

export default new Vapi({
  baseURL: '/api',
  state: {
    experiments: []
  }
}).get({
  action: 'getExperiments',
  property: 'experiments',
  path: '/admin/experiments',
  onSuccess: (state, payload) => {
    state.experiments = _.map(payload.data, (experimentData) => new ExperimentDefinitionModel(experimentData))
  }
}).getStore()
