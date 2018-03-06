import Vapi from 'vuex-rest-api'
import UserModel from '../model/user/user'

export default new Vapi({
  baseURL: '/api',
  state: new UserModel({
    root: false,
    anonymous: true
  })
}).get({
  action: 'getUser',
  path: '/admin/user',
  property: 'user',
  onSuccess: (state, payload) => {
    state.user = new UserModel(payload.data)
    console.log('state.user', state.user)
  },
  onError: (state, error) => {
    console.log(`Oops, there was following error: ${error}`)
    state.user = null
  }
}).getStore()
