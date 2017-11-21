import Vue from 'vue'
import Vuex from 'vuex'

import experiments from './experiments'

Vue.use(Vuex)

export default new Vuex.Store(experiments)
