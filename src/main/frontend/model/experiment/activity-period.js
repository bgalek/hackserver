import { Record } from 'immutable'
import moment from 'moment'

const ActivityPeriodRecord = Record({
  activeFrom: null,
  activeTo: null
})

const DEFAULT_FORMAT = 'MMMM Do YYYY, hh:mm:ss'

export default class ActivityPeriodModel extends ActivityPeriodRecord {
  constructor (periodObject) {
    periodObject = Object.assign({}, periodObject)
    super(periodObject)
  }

  fromDateShortString () {
    return moment(this.activeFrom).fromNow()
  }

  fromDateString () {
    return moment(this.activeFrom).format(DEFAULT_FORMAT)
  }

  toDateString () {
    return moment(this.activeTo).format(DEFAULT_FORMAT)
  }
};