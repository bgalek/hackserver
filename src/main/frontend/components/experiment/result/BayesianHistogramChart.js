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
      console.log(histogramData.values)
      this.renderChart({
        labels: histogramData.values.map(v => v.toFixed(2)),
        datasets: [
          {
            label: '',
            backgroundColor: histogramData.values.map(v => v < 0 ? '#e62e00' : '#00b300'),
            data: histogramData.counts
          }
        ]
      },
      {
        legend: {
          display: false
        },
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          xAxes: [{
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
