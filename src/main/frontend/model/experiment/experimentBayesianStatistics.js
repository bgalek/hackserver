import { Record } from 'immutable'

const HistogramRecord = Record({
  frequencies: [],
  labels: [],
  values: [],
  variantName: ''
})

const HistogramMetadataRecord = Record({
  deviceClass: '',
  toDate: ''
})

const BayesianHistogramsRecord = Record({
  metadata: {},
  histograms: []
})

const ExperimentBayesianStatisticsRecord = Record({
  deviceStatistics: {}
})

export default class ExperimentBayesianStatisticsModel extends ExperimentBayesianStatisticsRecord {
  constructor (experimentBayesianStatisticsObject) {
    if (!experimentBayesianStatisticsObject) {
      super([])
      return
    }

    const byDevice = {}

    experimentBayesianStatisticsObject.forEach(item => {
      const device = item.metadata.deviceClass.startsWith('phone') ? 'smartphone' : item.metadata.deviceClass
      const metricName = item.metricName

      if (!byDevice[device]) {
        byDevice[device] = {metricStatistics: {}}
      }

      byDevice[device].metricStatistics[metricName] = new BayesianHistogramsRecord({
        histograms: item.histograms.map(it => new HistogramRecord(it)),
        metadata: new HistogramMetadataRecord(item.metadata)
      })
    })

    super({deviceStatistics: byDevice})
  }

  getForDevice (device) {
    return this.deviceStatistics[device] || {}
  }

  any () {
    return this.deviceStatistics.size !== 0
  }
}
