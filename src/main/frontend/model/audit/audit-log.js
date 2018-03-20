import { Record } from 'immutable'
import CommitDetails from './commit-details'

const AuditLogRecord = Record({
  experimentId: '',
  changes: []
})

export default class AuditLog extends AuditLogRecord {
  constructor (auditLog) {
    auditLog = Object.assign({}, auditLog)
    auditLog.changes = auditLog.changes.map(cd => new CommitDetails(cd))
    super(auditLog)
  }

  hasChanges () {
    return this.changes.length > 0
  }

  static empty () {
    return new this({experimentId: '', changes: []})
  }
}
