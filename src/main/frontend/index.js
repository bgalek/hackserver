import Vue from 'vue'
import Vuetify from 'vuetify'
import './stylus/main.styl'
import Clipboard from 'v-clipboard'
import axios from 'axios'
import App from './App'
import router from './router'
import store from './store'
import {manageAuth, getAccessToken, isLoggedIn} from './auth'

axios.interceptors.request.use(function (config) {
  if (isLoggedIn()) {
    const token = `Bearer ${getAccessToken()}`
    const methods = ['get', 'post', 'put']
    methods.forEach(method => { config.headers[method]['Authorization'] = token })
  }
  return config
}, function (error) {
  console.error(error)
  return Promise.reject(error)
})

async function main () {
  const configResponse = await axios('/api/env')
  const configuration = configResponse.data

  manageAuth(configuration)

  if (!configuration.securityEnabled || isLoggedIn()) {
    Vue.use(Vuetify)
    Vue.use(Clipboard)
    Vue.config.productionTip = false

    /* eslint-disable no-new */
    new Vue({
      el: '#app',
      store,
      router,
      template: '<App/>',
      components: {App}
    })
  }
}

main().catch(error => console.error(error))
