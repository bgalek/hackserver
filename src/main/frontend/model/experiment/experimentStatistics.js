import { Record } from 'immutable'

const ExperimentStatisticsRecord = Record({
  deviceStatistics: {}
})

export default class ExperimentStatisticsModel extends ExperimentStatisticsRecord {
  constructor (experimentStatisticsObject) {
    console.log('mapping stats')
    console.log(experimentStatisticsObject)
    const deviceStatistics = new Map(experimentStatisticsObject.map(it => {
      console.log(it)
      const stats = it
      const mappedMetrics = []

      _.forIn(stats.metrics, (metricValuePerVariant, metricName) => {
        console.log('1')
        let mappedVariants = []
        _.forIn(metricValuePerVariant, (metricValue, variantName) => {
          console.log('2')
          mappedVariants[(variantName !== 'base') ? 'push' : 'unshift']({
            variant: variantName,
            value: metricValue.value,
            diff: metricValue.diff,
            count: metricValue.count,
            pValue: metricValue.pValue
          })
        })
        console.log('3')
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
        console.log('4')
      })

      return [stats.device, {
        id: stats.id,
        device: stats.device,
        toDate: stats.toDate,
        metrics: mappedMetrics.sort((x, y) => x.order - y.order),
        metricsNotAvailable: stats.metrics && stats.metrics.length === 0
      }]

    }))

    console.log('creating stats')
    console.log(deviceStatistics)

    super({deviceStatistics: deviceStatistics})
  }

  getForDevice (device) {
    return this.deviceStatistics.get(device) || {}
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
