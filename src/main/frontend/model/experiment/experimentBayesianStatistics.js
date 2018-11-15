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
  metricStatistics: {}
})

export default class ExperimentBayesianStatisticsModel extends ExperimentBayesianStatisticsRecord {
  constructor (experimentBayesianStatisticsObject) {
    console.log("experimentBayesianStatisticsObject", experimentBayesianStatisticsObject)

    if (!experimentBayesianStatisticsObject) {
      super([])
    }

    //Map<String:{}>
    const byMetric = experimentBayesianStatisticsObject.reduce((groups, item) => {
      const metricName = item.metricName
      groups[metricName] = groups[metricName] || {deviceStatistics:{}}

      const device = item.metadata.deviceClass.startsWith('phone') ? 'smartphone' : item.metadata.deviceClass
      groups[metricName].deviceStatistics[device] = new BayesianHistogramsRecord({
          histograms: item.histograms.map(it => new HistogramRecord(it)),
          metadata: new HistogramMetadataRecord(item.metadata)
      })

      console.log("item", groups[metricName].deviceStatistics[device])

      return groups
    }, {})

    console.log("byMetric", byMetric)

   // super({deviceStatistics: deviceStatistics})
    super({metricStatistics: byMetric})
    console.log("this", this)
  }



  //TODO
  getForDevice (device) {
    return this.deviceStatistics.get(device) || {}
  }

  //TODO
  any () {
    return this.deviceStatistics.size !== 0
  }
}
