import Vue from 'vue'
import Vuex from 'vuex'

import experiments from './experiments'
import experiment from './experiment'
import experimentStatistics from './experimentStatistics'
import deleteExperiment from './deleteExperiment'
import startExperiment from './startExperiment'
import createExperiment from './createExperiment'
import { userPreferences } from './userPreferences'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    experiment,
    experiments,
    experimentStatistics,
    createExperiment,
    startExperiment,
    deleteExperiment,
    userPreferences
  }
})
