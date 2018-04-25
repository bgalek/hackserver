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
  editable: false,
  origin: '',
  definition: null,
  reportingType: null,
  eventDefinitions: [],
  percentage: null,
  internalVariantName: null,
  variantNames: [],
  deviceClass: null
})

export default class ExperimentModel extends ExperimentRecord {
  constructor (experimentObject) {
    experimentObject = Object.assign({}, experimentObject)
    experimentObject.activityPeriod = experimentObject.activityPeriod && new ActivityPeriod(experimentObject.activityPeriod)
    experimentObject.variants = List(experimentObject.renderedVariants).map((variant, i) => new ExperimentVariantModel(variant, i)).toArray()
    experimentObject.hasBase = _.includes(_.map(experimentObject.variants, v => v.name), 'base')
    experimentObject.isMeasured = experimentObject.hasBase && experimentObject.reportingEnabled
    experimentObject.groups = List(experimentObject.groups)
    experimentObject.variantNames = List(experimentObject.variantNames)
    experimentObject.eventDefinitions = List(experimentObject.eventDefinitions)
    super(experimentObject)
  }

  whenStartedOrEnded () {
    if (this.activityPeriod) {
      if (this.status === 'PLANNED') {
        return 'to be started ' + this.activityPeriod.fromDateShortString()
      } else
      if (this.status === 'ENDED') {
        return 'ended ' + this.activityPeriod.toDateShortString()
      } else {
        return 'started ' + this.activityPeriod.fromDateShortString()
      }
    }
  }

  groupsInfo () {
    return this.groups.join(', ')
  }

  getBaseVariant () {
    return this.variants.find(it => it.isBase())
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
    return this.origin === 'MONGO' && this.status !== 'ENDED'
  }

  canChangeEventDefinitions () {
    return this.reportingType === 'FRONTEND' && this.status !== 'ENDED'
  }

  canRunLifecycleCommand () {
    return this.canBeStarted() ||
      this.canBeStopped() ||
      this.canBePaused() ||
      this.canBeResumed()
  }
};
