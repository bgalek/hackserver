import { Record } from 'immutable'
import _ from 'lodash'

const ExperimentStatisticsRecord = Record({
  deviceStatistics: {}
})

export default class ExperimentStatisticsModel extends ExperimentStatisticsRecord {
  constructor (experimentStatisticsObject) {
    const deviceStatistics = new Map(experimentStatisticsObject.map(it => {
      const stats = it
      const mappedMetrics = []

      _.forIn(stats.metrics, (metricValuePerVariant, metricName) => {
        let mappedVariants = []
        _.forIn(metricValuePerVariant, (metricValue, variantName) => {
          mappedVariants[(variantName !== 'base') ? 'push' : 'unshift']({
            variant: variantName,
            value: metricValue.value,
            diff: metricValue.diff,
            count: metricValue.count,
            pValue: metricValue.pValue
          })
        })
        mappedMetrics.push({
          'key': metricName,
          'variants': mappedVariants,
          'order': {
            'tx_visit': 1,
            'tx_avg': 2,
            'gmv': 3,
            'tx_cmuid': 4,
            'gmv_cmuid': 5,
            'tx_daily': 6,
            'tx_avg_daily': 7,
            'gmv_daily': 8
          }[metricName]
        })
      })
      const device = stats.device.startsWith('phone') ? 'smartphone': stats.device

      return [device, {
        id: stats.id,
        device: device,
        toDate: stats.toDate,
        metrics: mappedMetrics.sort((x, y) => x.order - y.order),
        metricsNotAvailable: stats.metrics && stats.metrics.length === 0
      }]
    }))

    super({deviceStatistics: deviceStatistics})
  }

  getForDevice (device) {
    return this.deviceStatistics.get(device.startsWith('phone') ? 'smartphone': device) || {}
  }

  any () {
    console.log(this.deviceStatistics.size)
    return this.deviceStatistics.size !== 0
  }

  isPending () {
    return false
  }

  getError () {
    return false
  }

  isReady () {
    return true
  }
}
