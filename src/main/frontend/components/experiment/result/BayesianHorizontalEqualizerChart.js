import { HorizontalBar } from 'vue-chartjs'

export default {
  extends: HorizontalBar,

  props: ['equalizerData'],

  mounted () {
    if (this.equalizerData) {
      this.printEqualizer(this.equalizerData)
    }
  },

  methods: {
    printEqualizer (equalizerData) {
      const boxesUp = Math.max(...equalizerData.bars.map(x => x.improvingProbabilities.length))
      const RADIUS = Math.max(boxesUp, ...equalizerData.bars.map(x => x.worseningProbabilities.length))
      this.renderChart(this.equalizerDataToDataSets(equalizerData, RADIUS),
        {
          legend: {
            display: false
          },
          responsive: false,
          maintainAspectRatio: false,
          scales: {
            xAxes: [{
              display: false,
              stacked: true,
              barPercentage: 1.0,
              categoryPercentage: 1.0,
              ticks: {
                autoskip: false
              },
              gridLines: {
                display: false
              }
            }],
            yAxes: [{
              display: false,
              stacked: true,
              ticks: {
                display: false,
                min: -1 * RADIUS,
                max: RADIUS,
                callback: function (value, index, values) {
                  let sign = value > 0 ? '+' : ''
                  return `${sign}${equalizerData.metadata.boxSize * value * 100.0}%`
                }
              },
              gridLines: {
                display: false
              }
            }]
          },
          hover: {
            mode: 'x'
          },
          tooltips: {
            enabled: false,
            callbacks: {
              title: function (item, data) {
                if (item[0].datasetIndex < RADIUS) {
                  return `Probability of decrease by ${(item[0].datasetIndex * equalizerData.metadata.boxSize) * 100.0}% or more`
                } else {
                  return `Probability of increase by ${((item[0].datasetIndex - RADIUS) * equalizerData.metadata.boxSize) * 100.0}% or more`
                }
              },
              label: function (item, data) {
                let p = 0.0
                if (item.datasetIndex < RADIUS) {
                  p = equalizerData.bars[item.index].worseningProbabilities[item.datasetIndex]
                } else {
                  p = equalizerData.bars[item.index].improvingProbabilities[item.datasetIndex - RADIUS]
                }
                if (p > 0) {
                  return `${(p * 100.0).toFixed(2)}%`
                } else {
                  return '~0%'
                }
              }
            }
          }
        })
    }
  },

  watch: {
    equalizerData (equalizerData) {
      if (this.$data._chart) {
        this.$data._chart.destroy()
      }
      if (equalizerData) {
        this.printEqualizer(equalizerData)
      }
    }
  }
}
