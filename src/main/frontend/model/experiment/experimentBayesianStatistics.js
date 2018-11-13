import { Record } from 'immutable'

const ExperimentBayesianStatisticsRecord = Record({
  deviceStatistics: {}
})

export default class ExperimentBayesianStatisticsModel extends ExperimentBayesianStatisticsRecord {
  constructor (experimentBayesianStatisticsObject) {
    const deviceStatistics = new Map(experimentBayesianStatisticsObject.map(it => {
      return [it.metadata.deviceClass.startsWith('phone') ? 'smartphone' : it.metadata.deviceClass, it]
    }))

    super({deviceStatistics: deviceStatistics})
  }

  getForDevice (device) {
    return this.deviceStatistics.get(device) || {}
  }

  any () {
    return this.deviceStatistics.size !== 0
  }
}
