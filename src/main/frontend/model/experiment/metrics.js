import { Record, List } from 'immutable'

const MetricRecord = Record({
  key: '',
  label: '',
  isLegacy: true,
  isBinary: false
})

export class MetricModel extends MetricRecord {
  constructor (key, label, flags) {
    super({
      key: key,
      label: label,
      isLegacy: flags && flags.legacy === true,
      isBinary: flags && flags.binary === true
    })
  }
}

const allMetrics = List(
  [
    new MetricModel('tx_visit', 'Visit conversion', {binary: true}),
    new MetricModel('tx_cmuid', 'Client(cmuid) conversion', {binary: true}),
    new MetricModel('gmv_cmuid', 'GMV per Client(cmuid) '),
    new MetricModel('gmv', 'GMV per visit'),
    new MetricModel('tx_daily', 'Daily conversion - BETA', {legacy: true}),
    new MetricModel('tx_avg', 'Transactions per visit', {legacy: true}),
    new MetricModel('tx_avg_daily', 'Transactions daily - BETA', {legacy: true}),
    new MetricModel('gmv_daily', 'GMV daily - BETA', {legacy: true})
  ]
)

export function getMetricByKey (metricKey) {
  return allMetrics.find(v => v.key === metricKey)
}

export function getMetricLabelByKey (metricKey) {
  return getMetricByKey(metricKey) && getMetricByKey(metricKey).label
}

export function nonLegacyMetrics () {
  return allMetrics.filter(v => !v.isLegacy).toArray()
}

export function allMetricLabels () {
  const result = {}
  allMetrics.forEach(v => {
    result[v.key] = v.label
  })
  return result
}
