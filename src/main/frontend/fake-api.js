const moment = require('moment')

const appState = {
  state: {
    diffSign: -1,
    metrics: ['tx_visit', 'gmv'],
    devices: ['desktop', 'smartphone', 'tablet', 'all'],
    metricValueForDevice: {
      'desktop': 0.1,
      'smartphone': 0.2,
      'tablet': 0.3,
      'all': 0.4
    },
    experiments: {
      metrum: {
        id: 'metrum',
        variants: [{name: 'metrum'}, {name: 'base'}],
        durationDays: 10,
        toDate: moment().add(-1, 'day').toDate()
      },
      sgLite: {
        id: 'sgLite',
        variants: [{name: 'base'}, {name: 'lite'}, {name: 'superlite'}],
        durationDays: 5,
        toDate: moment().add(-1, 'day').toDate()
      },
      header: {
        id: 'header',
        variants: [{name: 'base'}, {name: 'v1'}, {name: 'v2'}, {name: 'v3'}],
        durationDays: 1,
        toDate: moment().add(-1, 'day').toDate()
      }
    }
  },

  getExperiments: function () {
    return Object.values(this.state.experiments)
  },

  getExperiment: function (experimentId) {
    return this.state.experiments[experimentId]
  },

  getExperimentDurationMillis: function(experimentId, toDate) {
    // ---------B----C-A
    let experiment = this.getExperiment(experimentId)
    let durationMillis = experiment.durationDays * 24 * 3600 * 1000
    return experiment.durationDays * 24 * 3600 * 1000 - (experiment.toDate.getTime() - toDate.getTime())
  },

  getExperimentToDateAsString: function(experimentId) {
    return this.dateToString(this.getExperiment(experimentId).toDate)
  },

  dateToString: function(dt) {
    let year = dt.getFullYear()
    let month = dt.getMonth()+1 < 10 ? `0${dt.getUTCMonth()+1}`: dt.getUTCMonth()+1
    let day = dt.getDate() < 10 ? `0${dt.getUTCDate()}`: dt.getUTCDate()
    return `${year}-${month}-${day}`
  },

  generateMetricsForExperiment: function(experimentId, pickedDevice, durationDays) {
    let result = {}
    this.state.metrics.forEach((metricName) => {
      result[metricName] = this.generateMetricForAllVariants(experimentId, pickedDevice, durationDays)
    })
    return result
  },

  getExperimentVariantNames: function (experimentId) {
    return this.getExperiment(experimentId).variants.map(e => {return e.name})
  },

  getMetricValueForDeviceAndDuration: function (device, durationDays) {
    this.state.diffSign *= -1
    let diffVal = this.state.metricValueForDevice[device] * this.state.diffSign + Math.random() / 10
    return {
      value: this.state.metricValueForDevice[device],
      pValue: this.state.metricValueForDevice[device],
      diff: diffVal,
      count: Math.floor(this.state.metricValueForDevice[device] * durationDays * 10000)
    }
  },

  generateMetricForAllVariants: function(experimentId, pickedDevice, durationDays) {
    let result = {}
    this.getExperimentVariantNames(experimentId).forEach(variantName => {
      result[variantName] = this.getMetricValueForDeviceAndDuration(pickedDevice, durationDays)
    })
    return result
  },

  getStatistics: function (experimentId, device, toDate) {
    // todo handle missing experiments
    // todo handle missing devices
    let duration = this.getExperimentDurationMillis(experimentId, toDate)
    return {
      id: experimentId,
      duration: duration,
      toDate: this.dateToString(toDate),
      device: device,
      metrics: this.generateMetricsForExperiment(experimentId, device, Math.floor(duration / (1000 * 24 * 3600)))
    }
  }
}

module.exports.appState = appState
