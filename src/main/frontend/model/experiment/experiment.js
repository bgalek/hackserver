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
  origin: ''
})

export default class ExperimentModel extends ExperimentRecord {
  constructor (experimentObject) {
    experimentObject = Object.assign({}, experimentObject)
    experimentObject.activityPeriod = experimentObject.activityPeriod && new ActivityPeriod(experimentObject.activityPeriod)
    experimentObject.variants = List(experimentObject.variants).map((variant, i) => new ExperimentVariantModel(variant, i)).toArray()
    experimentObject.hasBase = _.includes(_.map(experimentObject.variants, v => v.name), 'base')
    experimentObject.isMeasured = experimentObject.hasBase && experimentObject.reportingEnabled
    experimentObject.groups = List(experimentObject.groups)
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

  fromDateString () {
    return this.activityPeriod && this.activityPeriod.fromDateString()
  }

  toDateString () {
    return this.activityPeriod && this.activityPeriod.toDateString()
  }

  groupsInfo () {
    return this.groups.join(', ')
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

  canRunAnyCommand () {
    return this.origin !== 'stash' &&
      (this.canBeStarted() ||
       this.canBeStopped() ||
       this.canBePaused() ||
       this.canBeResumed())
  }

  getBaseVariant () {
    return this.variants.find(it => it.isBase())
  }

  getBaseDeviceClass () {
    if (this.getBaseVariant()) {
      return this.getBaseVariant().deviceClass
    }
  }
};
