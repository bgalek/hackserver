import { Record } from 'immutable'
import moment from 'moment'

const CommitDetailsRecord = Record({
  author: '',
  date: '',
  changelog: ''
})

export default class CommitDetails extends CommitDetailsRecord {
  constructor (commitDetails) {
    commitDetails = Object.assign({}, commitDetails)
    super(commitDetails)
  }

  formattedDate () {
    return moment(this.date).format('MMMM Do YYYY, HH:mm:ss')
  }
}
