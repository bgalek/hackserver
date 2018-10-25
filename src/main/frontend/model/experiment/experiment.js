import { Record, List } from 'immutable'
import _ from 'lodash'
import moment from 'moment'
import ExperimentVariantModel from './experiment-variant'
import ActivityPeriod from './activity-period'
import ExperimentGroup from './experiment-group'
import ExperimentGoal from './experiment-goal'

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
  definition: null,
  reportingType: null,
  eventDefinitions: [],
  percentage: null,
  internalVariantName: null,
  variantNames: [],
  deviceClass: null,
  experimentGroup: null,
  bonferroniCorrection: 1,
  maxPossibleAllocation: 0,
  lastStatusChange: null,
  goal: null
})

export default class ExperimentModel extends ExperimentRecord {
  constructor (experimentObject) {
    experimentObject = Object.assign({}, experimentObject)

    experimentObject.activityPeriod = experimentObject.activityPeriod &&
      new ActivityPeriod(experimentObject.activityPeriod)

    experimentObject.variants = List(experimentObject.renderedVariants).map((variant, i) =>
      new ExperimentVariantModel(experimentObject.status === 'DRAFT', variant, i)).toArray()

    experimentObject.experimentGroup = experimentObject.experimentGroup &&
      new ExperimentGroup(experimentObject.experimentGroup)

    experimentObject.goal = experimentObject.goal &&
      new ExperimentGoal(experimentObject.goal)

    experimentObject.hasBase = _.includes(_.map(experimentObject.variants, v => v.name), 'base')
    experimentObject.isMeasured = experimentObject.hasBase && experimentObject.reportingEnabled
    experimentObject.groups = List(experimentObject.groups)
    experimentObject.variantNames = List(experimentObject.variantNames)
    experimentObject.eventDefinitions = List(experimentObject.eventDefinitions)

    super(experimentObject)
  }

  desiredAlpha () {
    return 0.05
  }

  usedAlpha () {
    return (this.desiredAlpha() / this.bonferroniCorrection).toFixed(4)
  }

  getLastStatusChangeMoment () {
    return this.lastStatusChange && moment(this.lastStatusChange).fromNow()
  }

  getStatusDesc () {
    const map = new Map([
      ['PLANNED', 'to be started'],
      ['DRAFT', 'created as draft'],
      ['ACTIVE', 'started'],
      ['PAUSED', 'paused'],
      ['ENDED', 'ended'],
      ['FULL_ON', 'made full-on']
    ])

    return map.get(this.status)
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
    return this.status === 'DRAFT'
  }

  canBeStopped () {
    return ['ACTIVE', 'FULL_ON'].includes(this.status)
  }

  canBeFullOn () {
    return this.status === 'ACTIVE' && !this.isInGroup()
  }

  canBePaused () {
    return this.status === 'ACTIVE'
  }

  canBeResumed () {
    return this.status === 'PAUSED'
  }

  canBeProlonged () {
    return this.status !== 'DRAFT' && this.status !== 'FULL_ON'
  }

  canJoinAnyGroup () {
    return this.status === 'DRAFT'
  }

  canChangeVariants () {
    return !this.isEffectivelyEnded()
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
      ['DRAFT', 'ACTIVE'].includes(this.status)
  }

  canRunLifecycleCommand () {
    return this.canBeStarted() ||
      this.canBeStopped() ||
      this.canBePaused() ||
      this.canBeResumed() ||
      this.canBeProlonged()
  }
};
