import { Record, List } from 'immutable'
import _ from 'lodash'
import moment from 'moment'
import ExperimentVariantModel from './experiment-variant'
import ActivityPeriod from './activity-period'
import ExperimentGroup from './experiment-group'
import ExperimentGoal from './experiment-goal'

const ExperimentDefinitionRecord = Record({
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
  goal: null,
  tags: [],
  customMetricDefinition: null
})

export default class ExperimentDefinitionModel extends ExperimentDefinitionRecord {
  constructor (experimentDefinitionObject) {
    experimentDefinitionObject = Object.assign({}, experimentDefinitionObject)

    experimentDefinitionObject.activityPeriod = experimentDefinitionObject.activityPeriod &&
      new ActivityPeriod(experimentDefinitionObject.activityPeriod)

    experimentDefinitionObject.variants = List(experimentDefinitionObject.renderedVariants).map((variant, i) =>
      new ExperimentVariantModel(experimentDefinitionObject.status === 'DRAFT', variant, i)).toArray()

    experimentDefinitionObject.experimentGroup = experimentDefinitionObject.experimentGroup &&
      new ExperimentGroup(experimentDefinitionObject.experimentGroup)

    experimentDefinitionObject.goal = experimentDefinitionObject.goal &&
      new ExperimentGoal(experimentDefinitionObject.goal)

    experimentDefinitionObject.hasBase = _.includes(_.map(experimentDefinitionObject.variants, v => v.name), 'base')
    experimentDefinitionObject.isMeasured = experimentDefinitionObject.hasBase && experimentDefinitionObject.reportingEnabled
    experimentDefinitionObject.groups = List(experimentDefinitionObject.groups)
    experimentDefinitionObject.variantNames = List(experimentDefinitionObject.variantNames)
    experimentDefinitionObject.eventDefinitions = List(experimentDefinitionObject.eventDefinitions)
    experimentDefinitionObject.tags = experimentDefinitionObject.tags.map(it => it.id)

    super(experimentDefinitionObject)
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
    return (this.getBaseVariant() && this.getBaseVariant().deviceClass) || 'all'
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
    return !['DRAFT', 'FULL_ON', 'ENDED'].includes(this.status)
  }

  canJoinAnyGroup () {
    return this.status === 'DRAFT'
  }

  canChangeVariants () {
    return !this.isEffectivelyEnded()
  }

  canBeUngrouped () {
    return this.isInGroup() && ['DRAFT', 'ENDED'].includes(this.status)
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

  getInitialDevice () {
    const baseClass = this.getBaseDeviceClass()

    if (baseClass === 'desktop') {
      return baseClass
    }

    if (baseClass && baseClass.startsWith('phone')) {
      return 'smartphone'
    }

    return 'all'
  }
};
