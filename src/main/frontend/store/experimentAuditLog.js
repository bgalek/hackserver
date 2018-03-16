import Vapi from 'vuex-rest-api'
import AuditLog from '../model/audit/audit-log'

export default new Vapi({
  baseURL: '/api',
  state: {
    auditLog: AuditLog.empty()
  }
}).get({
  action: 'getExperimentAuditLog',
  path: ({experimentId}) => `/admin/experiments/${experimentId}/audit-log`,
  onSuccess: (state, paylaod) => {
    state.auditLog = new AuditLog(paylaod.data)
  },
  onError: (state, error) => {
    state.error = error
    state.auditLog = AuditLog.empty()
  }
}).getStore()
