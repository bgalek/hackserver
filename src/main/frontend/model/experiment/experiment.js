import { Record, List } from 'immutable'
import _ from 'lodash'

import ExperimentVariantModel from './experiment-variant'
import ActivityPeriod from './activity-period'

const ExperimentRecord = Record({
  id: null,
  variants: [],
  description: '',
  documentLink: '',
  author: '',
  groups: [],
  activityPeriod: null,
  reportingEnabled: true,
  hasBase: true,
  isMeasured: true,
  status: '',
  measurements: Record({
    lastDayVisits: 0
  }),
  bayesianEqualizer: null,
  editable: false,
  origin: '',
  definition: null,
  reportingType: null,
  eventDefinitions: [],
  percentage: null,
  internalVariantName: null,
  variantNames: [],
  deviceClass: null,
  experimentGroup: null,
  bonferroniCorrection: 1
})

export default class ExperimentModel extends ExperimentRecord {
  constructor (experimentObject) {
    experimentObject = Object.assign({}, experimentObject)
    experimentObject.activityPeriod = experimentObject.activityPeriod && new ActivityPeriod(experimentObject.activityPeriod)
    experimentObject.variants = List(experimentObject.renderedVariants).map((variant, i) =>
      new ExperimentVariantModel(experimentObject.status === 'DRAFT', variant, i)).toArray()
    experimentObject.hasBase = _.includes(_.map(experimentObject.variants, v => v.name), 'base')
    experimentObject.isMeasured = experimentObject.hasBase && experimentObject.reportingEnabled
    experimentObject.groups = List(experimentObject.groups)
    experimentObject.variantNames = List(experimentObject.variantNames)
    experimentObject.eventDefinitions = List(experimentObject.eventDefinitions)
    if (experimentObject.experimentGroup) {
      experimentObject.experimentGroup = experimentObject.experimentGroup.id
    }
    super(experimentObject)
  }

  desiredAlpha () {
    return 0.05
  }

  usedAlpha () {
    return (this.desiredAlpha() / this.bonferroniCorrection).toFixed(4)
  }

  whenStartedOrEnded () {
    if (this.activityPeriod) {
      if (this.status === 'PLANNED') {
        return 'to be started ' + this.activityPeriod.fromDateShortString()
      } else
      if (this.status === 'ENDED') {
        return 'ended ' + this.activityPeriod.toDateShortString()
      } else if (this.status === 'FULL_ON') {
        return 'made full-on ' + this.activityPeriod.toDateShortString()
      } else {
        return 'started ' + this.activityPeriod.fromDateShortString()
      }
    }
  }

  isMultiVariant () {
    return this.variants.length > 2
  }

  groupsInfo () {
    return this.groups.join(', ')
  }

  getBaseVariant () {
    return this.variants.find(it => it.isBase())
  }

  getFirstVariant () {
    return this.variants.find(it => !it.isBase())
  }

  getBaseDeviceClass () {
    if (this.getBaseVariant()) {
      return this.getBaseVariant().deviceClass
    }
  }

  eventDefinitionsAvailable () {
    return this.reportingType === 'FRONTEND'
  }

  fromDateString () {
    return this.activityPeriod && this.activityPeriod.fromDateString()
  }

  toDateString () {
    return this.activityPeriod && this.activityPeriod.toDateString()
  }

  canBeStarted () {
    return this.origin === 'MONGO' && this.status === 'DRAFT'
  }

  canBeStopped () {
    return this.origin === 'MONGO' && ['ACTIVE', 'FULL_ON'].includes(this.status)
  }

  canBeFullOn () {
    return this.origin === 'MONGO' && this.status === 'ACTIVE'
  }

  canBePaused () {
    return this.origin === 'MONGO' && this.status === 'ACTIVE'
  }

  canBeResumed () {
    return this.origin === 'MONGO' && this.status === 'PAUSED'
  }

  canBeProlonged () {
    return this.origin === 'MONGO' && this.status === 'ACTIVE'
  }

  canChangeVariants () {
    return this.origin === 'MONGO' && !this.isEffectivelyEnded() && !this.isInGroup()
  }

  isEffectivelyEnded () {
    return this.status === 'ENDED' || this.status === 'FULL_ON'
  }

  canChangeEventDefinitions () {
    return this.reportingType === 'FRONTEND' && !this.isEffectivelyEnded()
  }

  isInGroup () {
    return this.experimentGroup
  }

  canBeGrouped () {
    return !this.isInGroup() &&
      this.origin === 'MONGO' &&
      ['BACKEND', 'FRONTEND', 'GTM'].includes(this.reportingType) &&
      ['DRAFT', 'ACTIVE', 'PAUSED'].includes(this.status)
  }

  canRunLifecycleCommand () {
    return this.canBeStarted() ||
      this.canBeStopped() ||
      this.canBePaused() ||
      this.canBeResumed()
  }
};
