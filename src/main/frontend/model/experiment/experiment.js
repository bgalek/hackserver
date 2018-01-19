import { Record, List } from 'immutable'
import moment from 'moment'
import _ from 'lodash'

import ExperimentVariantModel from './experiment-variant'
import ActivityPeriod from './activity-period'

const ExperimentRecord = Record({
  id: null,
  variants: [],
  description: '',
  author: '',
  groups: [],
  activityPeriod: null,
  reportingEnabled: true,
  hasBase: true,
  isMeasured: true,
  measurements: Record({
    lastDayVisits: 0
  })
})

export default class ExperimentModel extends ExperimentRecord {
  constructor (experimentObject) {
    experimentObject = Object.assign({}, experimentObject)
    experimentObject.activityPeriod = experimentObject.activityPeriod !== null ? new ActivityPeriod(experimentObject.activityPeriod) : null
    experimentObject.variants = List(experimentObject.variants).map((variant, i) => new ExperimentVariantModel(variant, i)).toArray()
    experimentObject.hasBase = _.includes(_.map(experimentObject.variants, v => v.name), 'base')
    experimentObject.isMeasured = experimentObject.hasBase && experimentObject.reportingEnabled
    experimentObject.groups = List(experimentObject.groups)
    experimentObject.measurements = experimentObject.measurements

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

  status () {
    if (this.activityPeriod === null) {
      return 'DRAFT'
    }
    if (moment(this.activityPeriod.activeFrom).isAfter(moment())) {
      return 'PLANNED'
    }
    if (moment(this.activityPeriod.activeTo).isBefore(moment())) {
      return 'ENDED'
    }
    return 'ACTIVE'
  }
};
