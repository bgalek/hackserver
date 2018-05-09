import { Bar } from 'vue-chartjs'

const RADIUS = 100

export default {
  extends: Bar,

  props: ['histogramData'],

  mounted () {
    if (this.histogramData) {
      this.printHistogram(this.histogramData)
    }
  },

  methods: {
    printHistogram (histogramData) {
      this.renderChart({
        labels: histogramData.values.map(x => `${(x * 100.0).toFixed(2)}%`),
        datasets: [
          {
            label: histogramData.labels[RADIUS - 1],
            backgroundColor: new Array(RADIUS * 2).fill('#e62e00'),
            data: histogramData.counts.slice(0, RADIUS).concat(new Array(RADIUS).fill(0))
          },
          {
            label: histogramData.labels[RADIUS],
            backgroundColor: new Array(RADIUS * 2).fill('#00b300'),
            data: new Array(RADIUS).fill(0).concat(histogramData.counts.slice(RADIUS, RADIUS * 2))
          }
        ]
      },
      {
        legend: {
          display: true
        },
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          xAxes: [{
            stacked: true,
            gridLines: {
              display: false
            },
            barPercentage: 1.11,
            categoryPercentage: 1.11,
            ticks: {
              autoskip: true
            }
          }],
          yAxes: [{
            ticks: {
              min: 0
            }
          }]
        },
        hover: {
          mode: 'x'
        },
        tooltips: {
          callbacks: {
            title: function (item, data) {
              if (item[0].xLabel.startsWith('-')) {
                return `Probability of decrease by ${item[0].xLabel} or more`
              } else {
                return `Probability of increase by ${item[0].xLabel} or more`
              }
            },
            label: function (item, data) {
              return histogramData.labels[item.index]
            }

          }
        }
      })
    }
  },

  watch: {
    histogramData (histogramData) {
      if (this.$data._chart) {
        this.$data._chart.destroy()
      }
      if (histogramData) {
        this.printHistogram(histogramData)
      }
    }
  }
}
