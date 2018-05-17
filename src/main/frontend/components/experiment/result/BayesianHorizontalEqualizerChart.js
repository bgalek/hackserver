import { HorizontalBar } from 'vue-chartjs'
import { equalizerDataToDataSets } from "./equalizerDataToDataSets";

export default {
  extends: HorizontalBar,

  props: ['equalizerData'],

  mounted () {
    if (this.equalizerData) {
      this.printEqualizer({ bars : [ this.equalizerData.joinedBar ], metadata: this.equalizerData.metadata })
    }
  },

  methods: {
    printEqualizer (equalizerData) {
      console.log('printEqualizer', equalizerData)
      const boxesUp = Math.max(...equalizerData.bars.map(x => x.improvingProbabilities.length))
      const RADIUS = Math.max(boxesUp, ...equalizerData.bars.map(x => x.worseningProbabilities.length))
      this.renderChart(equalizerDataToDataSets(equalizerData, RADIUS),
        {
          legend: {
            display: false
          },
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            xAxes: [{
              display: false,
              stacked: true,
              barThickness: 20,
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
              barThickness: 20,
              display: false,
              stacked: true,
              ticks: {
                display: false
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
            enabled: false
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
