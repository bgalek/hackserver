import { Bar } from 'vue-chartjs'

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
      const RADIUS = histogramData.labels.length / 2
      const variantName = histogramData.variantName.toUpperCase()
      const betterThanBase = Math.round(histogramData.labels[RADIUS]) + '%'
      const worseThanBase = Math.round(histogramData.labels[RADIUS - 1]) + '%'

      const redDataset = histogramData.frequencies.slice(0, RADIUS)
      const greenDataset = new Array(RADIUS).fill(0).concat(histogramData.frequencies.slice(RADIUS, RADIUS * 2))

      const labels = histogramData.values.map(x => (x * 100).toFixed(1) + '%')

      this.renderChart({
        labels: labels,
        datasets: [
          {
            label: `There is ${worseThanBase} risk that variant ${variantName} is worse than Base`,
            backgroundColor: new Array(RADIUS * 2).fill('#e62e00'),
            data: redDataset
          },
          {
            label: `There is ${betterThanBase} chance that variant ${variantName} is better than Base`,
            backgroundColor: new Array(RADIUS * 2).fill('#00b300'),
            borderWidth: 0,
            data: greenDataset
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
            type: 'category',
            scaleLabel: {
              display: true,
              labelString: 'Diff to Base'
            },
            stacked: true,
            gridLines: {
              display: false
            },
            barPercentage: 0.9,
            categoryPercentage: 1,
            ticks: {
              autoskip: true
            }
          }],
          yAxes: [{
            scaleLabel: {
              display: true,
              labelString: 'Frequency'
            },
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
                return `Risk of decreasing Diff to Base by ${item[0].xLabel} or more`
              } else {
                return `Chance of increasing Diff to Base by ${item[0].xLabel} or more`
              }
            },
            label: function (item, data) {
              return histogramData.labels[item.index] + '%'
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
