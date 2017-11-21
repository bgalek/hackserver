import Vue from 'vue'

import Vuetify from 'vuetify'
import './stylus/main.styl'

import Clipboard from 'v-clipboard'

import App from './App'
import router from './router'
import store from './store'

Vue.use(Vuetify)
Vue.use(Clipboard)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  store,
  router,
  template: '<App/>',
  components: { App }
})
