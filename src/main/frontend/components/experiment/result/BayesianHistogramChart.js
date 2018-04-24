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
      this.renderChart({
        labels: histogramData.values,
        datasets: [
          {
            label: '',
            backgroundColor: histogramData.values.map(v => v < 0 ? 'red' : 'green'),
            data: histogramData.counts
          }
        ]
      },
      {
        responsive: true,
        scales: {
          xAxes: [{
            gridLines: {
              display: false
            },
            barPercentage: 1.01,
            categoryPercentage: 1.01
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
