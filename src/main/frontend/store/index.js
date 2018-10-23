import Vue from 'vue'
import Vuex from 'vuex'

import experiments from './experiments'
import experimentGroups from './experimentGroups'
import experiment from './experiment'
import experimentAuditLog from './experimentAuditLog'
import experimentStatistics from './experimentStatistics'
import bayesianHistograms from './bayesianHistograms'
import bayesianEqualizer from './bayesianEqualizer'
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
    experimentGroups,
    experimentAuditLog,
    experimentStatistics,
    bayesianHistograms,
    bayesianEqualizer,
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
    calculateSampleSize
  }
})
