import Vue from 'vue'
import Vuex from 'vuex'

import experiments from './experiments'
import experimentGroups from './experimentGroups'
import experimentAuditLog from './experimentAuditLog'
import experimentStatistics from './experimentStatistics'
import deleteExperiment from './deleteExperiment'
import startExperiment from './startExperiment'
import createExperiment from './createExperiment'
import addExperimentToGroup from './addExperimentToGroup'
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
import makeExperimentFullOn from './makeExperimentFullOn'
import calculateSampleSize from './calculateSampleSize'
import experimentStore from './experimentStore'
import experimentTagStore from './experimentTagStore'

Vue.use(Vuex)

export default new Vuex.Store({
  plugins: [
    createPersistedState({
      key: 'chi-storage',
      paths: ['userPreferences']
    })],
  modules: {
    experimentStore,
    experiments,
    experimentGroups,
    experimentAuditLog,
    experimentStatistics,
    createExperiment,
    addExperimentToGroup,
    startExperiment,
    deleteExperiment,
    userPreferences,
    stopExperiment,
    makeExperimentFullOn,
    pauseExperiment,
    resumeExperiment,
    prolongExperiment,
    updateExperimentDescriptions,
    updateExperimentVariants,
    updateExperimentEventDefinitions,
    user,
    calculateSampleSize,
    experimentTagStore
  }
})
