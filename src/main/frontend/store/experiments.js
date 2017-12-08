import Vapi from 'vuex-rest-api'
import _ from 'lodash'
import ExperimentModel from '../model/experiment/experiment'

export default new Vapi({
  baseURL: '/api',
  state: {
    experiments: []
  }
}).get({
  action: 'getExperiments',
  property: 'experiments',
  path: '/experiments/v1',
  onSuccess: (state, payload) => {
    state.experiments = _.filter(
      _.map(payload.data, (experimentData) => new ExperimentModel(experimentData)),
      (experiment) => experiment.hasBase
    )
  }
}).getStore()
