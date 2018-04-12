import { Record, List } from 'immutable'
import _ from 'lodash'

import ExperimentVariantModel from './experiment-variant'
import ActivityPeriod from './activity-period'
import ExperimentDefinitionModel from './experiment-definition'

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
  definition: null
})

export default class ExperimentModel extends ExperimentRecord {
  constructor (experimentObject) {
    experimentObject = Object.assign({}, experimentObject)
    experimentObject.activityPeriod = experimentObject.activityPeriod && new ActivityPeriod(experimentObject.activityPeriod)
    experimentObject.variants = List(experimentObject.variants).map((variant, i) => new ExperimentVariantModel(variant, i)).toArray()
    experimentObject.hasBase = _.includes(_.map(experimentObject.variants, v => v.name), 'base')
    experimentObject.isMeasured = experimentObject.hasBase && experimentObject.reportingEnabled
    experimentObject.groups = List(experimentObject.groups)
    experimentObject.definition = experimentObject.definition && new ExperimentDefinitionModel(experimentObject.definition)
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
    return !!this.definition && this.definition.canBeStarted()
  }

  canBeStopped () {
    return !!this.definition && this.definition.canBeStopped()
  }

  canBePaused () {
    return !!this.definition && this.definition.canBePaused()
  }

  canBeResumed () {
    return !!this.definition && this.definition.canBeResumed()
  }

  canBeProlonged () {
    return !!this.definition && this.definition.canBeProlonged()
  }

  canRunLifecycleCommand () {
    return !!this.definition && this.definition.canRunLifecycleCommand()
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
    return this.definition && this.definition.reportingType === 'FRONTEND'
  }

  reportingType () {
    return (this.definition && this.definition.reportingType) || 'BACKEND'
  }
};
