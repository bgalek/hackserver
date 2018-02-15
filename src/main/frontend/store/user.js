import Vapi from 'vuex-rest-api'

export default new Vapi({
  baseURL: '/api',
  state: {
    isLoggedIn: false
  }
}).get({
  action: 'getUser',
  path: '/admin/user',
  property: 'isLoggedIn',
  onSuccess: (state, payload) => {
    state.isLoggedIn = !payload.data.anonymous || payload.data.root
    console.log(state.isLoggedIn)
  }
}).getStore()
