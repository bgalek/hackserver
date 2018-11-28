import Vue from 'vue'
import Vuex from 'vuex'
import VueAxios from 'vue-axios'
import { VueAuthenticate } from 'vue-authenticate'
import axios from 'axios'

Vue.use(Vuex)
Vue.use(VueAxios, axios)

function getRedirectUri () {
  return window.location.origin + '/'
}

const vueAuth = new VueAuthenticate(Vue.prototype.$http, {
  loginUrl: '/',
  providers: {
    oauth2: {
      url: '/#/',
      clientId: 'chi-server',
      authorizationEndpoint: 'https://oauth-dev.allegrogroup.com/auth/oauth/authorize',
      redirectUri: getRedirectUri(),
      responseParams: {
        code: 'authorization_code',
        clientId: 'client_id',
        redirectUri: 'redirect_uri'
      },
      responseType: 'token',
      popupOptions: { width: 800, height: 600 }
    }
  },
  // Disable the request interceptor a graphite can't handle it for now
  bindRequestInterceptor: function () {}
})

export default {
  state: {
    isAuthenticated: false,
    userName: 'anonymous'
  },

  getters: {
    // we don't want to cache this getter, so the router access it via method,
    isAuthenticated: (state) => () => {
      return state.isAuthenticated
    }
  },

  mutations: {
    setAuthentication (state, {isAuthenticated, userName}) {
      state.isAuthenticated = isAuthenticated
      state.userName = userName
    }
  },

  actions: {
    login (context, payload) {
      return vueAuth.authenticate('oauth2').then((response) => {
        const isAuthenticated = vueAuth.isAuthenticated()
        const userName =  vueAuth.getPayload() && vueAuth.getPayload()['full_name']
        const groups = vueAuth.getPayload() && vueAuth.getPayload()['authorities']

        console.log("authenticate:")
        console.log("isAuthenticated:" + isAuthenticated)
        console.log("userName:" + userName)

        context.commit('setAuthentication', {
          isAuthenticated: isAuthenticated,
          userName: userName
        })
      })
    },
    logout (context, payload) {
      return vueAuth.logout()
      /*return vueAuth.logout().then((response) => {
        context.commit('setAuthentication', {
          isAuthenticated: false,
          groups: [],
          userName: 'anonymous'
        })
      })(*/
    }
  }
}
