import { Record, List } from 'immutable'

const MetricRecord = Record({
  key: '',
  label: '',
  type: '',
  order: 0
})

export class MetricModel extends MetricRecord {
  constructor (key, label, type, order) {
    super({
      key: key,
      label: label,
      type: type,
      order: order
    })
  }

  isBinary () {
    return this.type === 'binary'
  }
}

const globalMetrics = List(
  [
    new MetricModel('tx_visit', 'Visit conversion', 'binary', 1),
    new MetricModel('gmv', 'GMV per visit', 'currency', 2),
    new MetricModel('tx_cmuid', 'Client(cmuid) conversion', 'binary', 3),
    new MetricModel('gmv_cmuid', 'GMV per Client(cmuid)', 'currency', 4)
  ]
)

export function getMetricByKey (metricKey) {
  return globalMetrics.find(v => v.key === metricKey) ||
       new MetricModel(metricKey, metricKey, 'binary', 0) // custom metric, probably
}

export function getMetricLabelByKey (metricKey) {
  return getMetricByKey(metricKey) && getMetricByKey(metricKey).label
}

export function globalMetricsArray () {
  return globalMetrics.toArray()
}
