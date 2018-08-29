import { Record } from 'immutable'
import moment from 'moment'

const ActivityPeriodRecord = Record({
  activeFrom: null,
  activeTo: null
})

const DEFAULT_FORMAT = 'MMMM Do YYYY, HH:mm'

export default class ActivityPeriodModel extends ActivityPeriodRecord {
  constructor (periodObject) {
    periodObject = Object.assign({}, periodObject)
    super(periodObject)
  }

  fromDateString () {
    return moment(this.activeFrom).format(DEFAULT_FORMAT)
  }

  toDateString () {
    return moment(this.activeTo).format(DEFAULT_FORMAT)
  }
};
