import { Record, List } from 'immutable'

import ActivityPeriod from './activity-period'

const ExperimentDefinitionRecord = Record({
  id: null,
  variantNames: [],
  internalVariantName: null,
  deviceClass: null,
  percentage: null,
  description: '',
  documentLink: '',
  author: '',
  groups: [],
  activityPeriod: null,
  reportingEnabled: true,
  status: ''
})

export default class ExperimentDefinitionModel extends ExperimentDefinitionRecord {
  constructor (experimentDefinitionObject) {
    experimentDefinitionObject = Object.assign({}, experimentDefinitionObject)
    experimentDefinitionObject.activityPeriod = experimentDefinitionObject.activityPeriod && new ActivityPeriod(experimentDefinitionObject.activityPeriod)
    experimentDefinitionObject.variantNames = List(experimentDefinitionObject.variantNames)
    experimentDefinitionObject.groups = List(experimentDefinitionObject.groups)
    super(experimentDefinitionObject)
  }

  fromDateString () {
    return this.activityPeriod && this.activityPeriod.fromDateString()
  }

  toDateString () {
    return this.activityPeriod && this.activityPeriod.toDateString()
  }

  canBeStarted () {
    return this.status === 'DRAFT'
  }

  canBeStopped () {
    return this.status === 'ACTIVE'
  }

  canBePaused () {
    return this.status === 'ACTIVE'
  }

  canBeResumed () {
    return this.status === 'PAUSED'
  }

  canBeProlonged () {
    return this.status === 'ACTIVE'
  }

  canChangeVariants () {
    return this.status !== 'ENDED'
  }

  canRunLifecycleCommand () {
    return this.canBeStarted() ||
      this.canBeStopped() ||
      this.canBePaused() ||
      this.canBeResumed()
  }
};
