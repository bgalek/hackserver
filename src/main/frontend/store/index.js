import Vue from 'vue'
import Vuex from 'vuex'

import experiments from './experiments'
import experiment from './experiment'
import experimentAuditLog from './experimentAuditLog'
import experimentStatistics from './experimentStatistics'
import bayesianStatistics from './bayesianStatistics'
import deleteExperiment from './deleteExperiment'
import startExperiment from './startExperiment'
import createExperiment from './createExperiment'
import stopExperiment from './stopExperiment'
import pauseExperiment from './pauseExperiment'
import resumeExperiment from './resumeExperiment'
import updateExperimentDescriptions from './updateExperimentDescriptions'
import updateExperimentVariants from './updateExperimentVariants'
import updateExperimentEventDefinitions from './updateExperimentEventDefinitions'
import prolongExperiment from './prolongExperiment'
import user from './user'
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
    experimentAuditLog,
    experimentStatistics,
    bayesianStatistics,
    createExperiment,
    startExperiment,
    deleteExperiment,
    userPreferences,
    stopExperiment,
    pauseExperiment,
    resumeExperiment,
    prolongExperiment,
    updateExperimentDescriptions,
    updateExperimentVariants,
    updateExperimentEventDefinitions,
    user
  }
})
