import Vue from 'vue'
import Vuex from 'vuex'

import experiments from './experiments'
import experiment from './experiment'
import experimentStatistics from './experimentStatistics'
import deleteExperiment from './deleteExperiment'
import startExperiment from './startExperiment'
import createExperiment from './createExperiment'
import stopExperiment from './stopExperiment'
import {userPreferences} from './userPreferences'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
  plugins: [
    createPersistedState({
      key: 'chi-storage',
      paths: ['userPreferences']
    })],
  modules: {
    experiment,
    experiments,
    experimentStatistics,
    createExperiment,
    startExperiment,
    deleteExperiment,
    userPreferences,
    stopExperiment
  }
})
