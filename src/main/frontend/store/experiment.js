import Vapi from 'vuex-rest-api'
import ExperimentModel from '../model/experiment/experiment'

export default new Vapi({
  baseURL: '/api',
  state: {
    experiment: new ExperimentModel({ })
  }
}).get({
  action: 'getExperiment',
  property: 'experiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}`,
  onSuccess: (state, payload) => {
    console.log('onSuccess', state, payload.data)
    state.experiment = new ExperimentModel(payload.data)
  }
}).getStore()
