import Vapi from 'vuex-rest-api'
import ExperimentModel from '../model/experiment/experiment'
import router from '../router'

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
    state.experiment = new ExperimentModel(payload.data)
  },
  onError: (state, error) => {
    console.log(`Oops, there was following error: ${error}`)
    state.experiment = null
    router.push('/404')
  }
}).getStore()
