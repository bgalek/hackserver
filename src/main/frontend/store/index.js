import Vue from 'vue'
import Vuex from 'vuex'

import experiments from './experiments'
import experiment from './experiment'

Vue.use(Vuex);

export default new Vuex.Store({
  modules: { experiment, experiments }
})
