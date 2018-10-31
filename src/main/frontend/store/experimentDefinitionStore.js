import Vapi from 'vuex-rest-api'
import ExperimentDefinitionModel from '../model/experiment/experimentDefinition'
import router from '../router'

export default new Vapi({
  baseURL: '/api',
  state: {
    experiment: new ExperimentDefinitionModel({ })
  }
}).get({
  action: 'getExperiment',
  property: 'experiment',
  path: ({experimentId}) => `/admin/experiments/${experimentId}`,
  onSuccess: (state, payload) => {
    state.experiment = new ExperimentDefinitionModel(payload.data)
  },
  onError: (state, error) => {
    console.log(`Oops, there was following error: ${error}`)
    state.experiment = null
    router.push('/404')
  }
}).getStore()
