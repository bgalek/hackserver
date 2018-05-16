import { Bar } from 'vue-chartjs'

export default {
  extends: Bar,

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
              stacked: true,
              barPercentage: 0.9,
              barThickness: 50,
              categoryPercentage: 0.9,
              ticks: {
                autoskip: true
              },
              gridLines: {
                display: false
              }
            }],
            yAxes: [{
              stacked: true,
              ticks: {
                display: true,
                min: -1 * RADIUS,
                max: 1 * RADIUS,
                callback: function (value, index, values) {
                  let sign = value > 0 ? '+' : ''
                  return `${sign}${equalizerData.boxSize * value * 100.0}%`
                }
              },
              gridLines: {
                display: true
              }
            }]
          },
          hover: {
            mode: 'x'
          },
          tooltips: {
            enabled: true,
            callbacks: {
              title: function (item, data) {
                if (item[0].datasetIndex < RADIUS) {
                  return `Probability of decrease by ${(item[0].datasetIndex * equalizerData.boxSize) * 100.0}% or more`
                } else {
                  return `Probability of increase by ${((item[0].datasetIndex - RADIUS) * equalizerData.boxSize) * 100.0}% or more`
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
    },

    equalizerDataToDataSets (equalizerData, RADIUS) {
      const labels = equalizerData.bars.map(v => v.variantName)
      const pluses = labels.map(() => 1)
      const minuses = labels.map(() => -1)

      let datasets = new Array(RADIUS).fill({
        label: '',
        backgroundColor: new Array(labels.length).fill('#eeeeee'),
        hoverBackgroundColor: new Array(labels.length).fill('#eeeeee'),
        borderColor: '#000000',
        borderWidth: 1,
        data: minuses
      }).concat(new Array(RADIUS).fill({
        label: '',
        backgroundColor: new Array(labels.length).fill('#eeeeee'),
        hoverBackgroundColor: new Array(labels.length).fill('#eeeeee'),
        borderColor: '#000000',
        borderWidth: 1,
        data: pluses
      })).map(x => Object.assign({}, x))

      equalizerData.bars.forEach((v, vidx) => {
        v.improvingProbabilities.forEach((diff, idx) => {
          const i = RADIUS + idx
          datasets[i].backgroundColor = datasets[i].backgroundColor.slice(0)
          datasets[i].backgroundColor[vidx] = this.probabilityToColor(diff, 'green')
          datasets[i].hoverBackgroundColor = datasets[i].backgroundColor
        })
        v.worseningProbabilities.forEach((diff, idx) => {
          const i = idx
          datasets[i].backgroundColor = datasets[i].backgroundColor.slice(0)
          datasets[i].backgroundColor[vidx] = this.probabilityToColor(diff, 'red')
          datasets[i].hoverBackgroundColor = datasets[i].backgroundColor
        })
      })

      return {
        labels,
        datasets
      }
    },

    probabilityToColor (probability, color) {
      const steps = [0.8, 0.5, 0.25, 0.1]
      const gray = '#eeeeee'
      const green = ['#00b300', '#6fc06f', '#b6dfb6', '#d7edd8']
      const red = ['#e62e00', '#ef8779', '#f9cac1', '#ecd3cc']
      const index = steps.findIndex(x => probability >= x)
      if (index === -1) {
        return gray
      }
      if (color === 'red') {
        return red[index]
      } else if (color === 'green') {
        return green[index]
      }
      return gray
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
