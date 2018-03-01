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

  fromDateShortString () {
    return this.activityPeriod && this.activityPeriod.fromDateShortString()
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
    return this.status !== 'PAUSED'
  }

  canBeResumed () {
    return this.status === 'PAUSED'
  }

  canBeProlonged () {
    return this.status === 'ACTIVE'
  }

  canBeDeleted () {
    return this.allowDelete
  }

  canRunAnyCommand () {
    return this.origin !== 'stash'
      && (this.canBeStarted() ||
          this.canBeDeleted() ||
          this.canBeStopped() ||
          this.canBePaused()  ||
          this.canBeResumed())
  }
};
