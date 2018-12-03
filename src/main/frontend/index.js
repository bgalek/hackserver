import Vue from 'vue'

import Vuetify from 'vuetify'
import './stylus/main.styl'

import Clipboard from 'v-clipboard'
import axios from 'axios'
import App from './App'
import createRouter from './router'
import createStore from './store'

Vue.use(Vuetify)
Vue.use(Clipboard)

Vue.config.productionTip = false

async function main () {
  const configResponse = await axios('/api/env')
  const configuration = configResponse.data
  console.log('configuration', configuration)

  const store = createStore(configuration)
  const router = createRouter(store)

  /* eslint-disable no-new */
  new Vue({
    el: '#app',
    store,
    router,
    template: '<App/>',
    components: { App }
  })
}

main().catch(error => console.error(error))
