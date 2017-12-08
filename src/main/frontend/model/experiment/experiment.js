import { Record, List } from 'immutable'
import moment from 'moment'

import ExperimentVariantModel from './experiment-variant'

const ExperimentRecord = Record({
  id: null,
  variants: [],
  description: '',
  owner: '',
  activeFrom: null,
  activeTo: null,
  reportingEnabled: true
})

const DEFAULT_FORMAT = 'MMMM Do YYYY, hh:mm:ss'

export default class ExperimentModel extends ExperimentRecord {
  constructor (experimentObject) {
    experimentObject = Object.assign({}, experimentObject)
    experimentObject.activeFrom = experimentObject.activeFrom ? new Date(experimentObject.activeFrom) : null
    experimentObject.activeTo = experimentObject.activeTo ? new Date(experimentObject.activeTo) : null
    experimentObject.variants = List(experimentObject.variants).map(variant => new ExperimentVariantModel(variant)).toArray()

    super(experimentObject)
  }

  fromDateString () {
    return this.activeFrom && moment(this.activeFrom).format(DEFAULT_FORMAT)
  }

  toDateString () {
    return this.activeTo && moment(this.activeTo).format(DEFAULT_FORMAT)
  }

  status () {
    if (this.activeFrom && moment(this.activeFrom).isAfter(moment())) {
      return 'PLANNED'
    }
    if (this.activeTo && moment(this.activeTo).isBefore(moment())) {
      return 'ENDED'
    }
    return 'ACTIVE'
  }
};
