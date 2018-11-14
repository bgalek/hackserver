import { Record } from 'immutable'
import _ from 'lodash'

const ExperimentStatisticsRecord = Record({
  deviceStatistics: {}
})

const ExperimentDeviceStatisticsRecord = Record({
  id: null,
  device: null,
  toDate: null,
  metrics: null,
  metricsNotAvailable: null
})

const ExperimentMetricRecord = Record({
  'key': null,
  'variants': null,
  'order': null
})

class ExperimentMetricModel extends ExperimentMetricRecord {
  constructor (experimentMetricObject) {
    super({
      'key': experimentMetricObject.metricName,
      'variants': experimentMetricObject.variants,
      'order': {
        'tx_visit': 1,
        'tx_avg': 2,
        'gmv': 3,
        'tx_cmuid': 4,
        'gmv_cmuid': 5,
        'tx_daily': 6,
        'tx_avg_daily': 7,
        'gmv_daily': 8
      }[experimentMetricObject.metricName]
    })
  }
}

const ExperimentVariantMetricRecord = Record({
  variant: null,
  value: null,
  diff: null,
  count: null,
  pValue: null
})

class ExperimentVariantMetricModel extends ExperimentVariantMetricRecord {
  constructor (experimentVariantMetricObject) {
    super({
      variant: experimentVariantMetricObject.variantName,
      value: experimentVariantMetricObject.metricValue.value,
      diff: experimentVariantMetricObject.metricValue.diff,
      count: experimentVariantMetricObject.metricValue.count,
      pValue: experimentVariantMetricObject.metricValue.pValue
    })
  }
}

class ExperimentDeviceStatisticsModel extends ExperimentDeviceStatisticsRecord {
  constructor (stats) {
    const mappedMetrics = []

    _.forIn(stats.metrics, (metricValuePerVariant, metricName) => {
      let mappedVariants = []

      _.forIn(metricValuePerVariant, (metricValue, variantName) => {
        mappedVariants[(variantName !== 'base') ? 'push' : 'unshift'](new ExperimentVariantMetricModel({
          variantName: variantName,
          metricValue: metricValue
        }))
      })

      mappedMetrics.push(new ExperimentMetricModel({
        'metricName': metricName,
        'variants': mappedVariants
      }))
    })

    const device = stats.device.startsWith('phone') ? 'smartphone' : stats.device
    const result = {
      id: stats.id,
      device: device,
      toDate: stats.toDate,
      metrics: mappedMetrics.sort((x, y) => x.order - y.order),
      metricsNotAvailable: stats.metrics && stats.metrics.length === 0
    }
    super(result)
  }
}

export default class ExperimentStatisticsModel extends ExperimentStatisticsRecord {
  constructor (experimentStatisticsObject) {
    super({
      deviceStatistics: new Map(experimentStatisticsObject.map(it => {
        const experimentDeviceStatistics = new ExperimentDeviceStatisticsModel(it)
        return [experimentDeviceStatistics.device, experimentDeviceStatistics]
      }))
    })
  }

  getForDevice (device) {
    return this.deviceStatistics.get(device.startsWith('phone') ? 'smartphone' : device) || {}
  }

  any () {
    return this.deviceStatistics.size !== 0
  }
}
