import Vue from 'vue'
import VueAxios from 'vue-axios'
import { VueAuthenticate } from 'vue-authenticate'
import axios from 'axios'

Vue.use(VueAxios, axios)

function getRedirectUri () {
  return window.location.origin + '/'
}

function buildVueAuth (configuration) {
  if (!configuration.securityEnabled) {
    return {
      isAuthenticated: () => true,
      authenticate: () => Promise.resolve({}),
      getPayload: () => ({
        full_name: 'Mad Kaz',
        authorities: []
      })
    }
  }

  return new VueAuthenticate(Vue.prototype.$http, {
    loginUrl: '/',
    providers: {
      oauth2: {
        url: '/#/',
        clientId: configuration.oauthClientId,
        authorizationEndpoint: configuration.userAuthorizationUri,
        redirectUri: getRedirectUri(),
        responseParams: {
          code: 'authorization_code',
          clientId: 'client_id',
          redirectUri: 'redirect_uri'
        },
        responseType: 'token',
        popupOptions: {width: 800, height: 600}
      }
    },
    bindRequestInterceptor: function () {
      this.$http.interceptors.request.use((config) => {
        if (this.isAuthenticated()) {
          config.headers['Authorization'] = [
            this.options.tokenType, this.getToken()
          ].join(' ')
        } else {
          delete config.headers['Authorization']
        }
        return config
      })
    }
  })
}

export default function createVuexAuthentication (configuration) {
  const vueAuth = buildVueAuth(configuration)

  const store = {
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
          const userName = vueAuth.getPayload() && vueAuth.getPayload()['full_name']

          if (isAuthenticated) {
            console.log('authenticated as ' + userName)
          } else {
            console.warn('authentication failed')
          }

          context.commit('setAuthentication', {
            isAuthenticated: isAuthenticated,
            userName: userName
          })
        })
      }
    }
  }

  return store
}
