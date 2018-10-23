<template>
  <v-form ref="goalEditingForm" v-model="formValid">

    <v-card  class="pa-3">
      <h3 class="pa-0">Experiment goal</h3>

      <v-alert v-for="error in errors"
               color="error" icon="warning" value="true" :key="error">
        {{ error }}
      </v-alert>

      <v-switch :label="hypothesisSwitchLabel"
                v-model="value.hasHypothesis"
                color="orange darken-3"
                class="pa-0">
      </v-switch>

      <div v-if="value.hasHypothesis">
        I want to improve the &nbsp;
        <v-select class="pa-0"
          style="width: 200px; display: inline-block"
          v-bind:items="metrics"
          v-model="value.leadingMetric"
          item-value="key"
          item-text="label"
        ></v-select>
        &nbsp; metric by at least &nbsp;
        <v-text-field class="pa-0"
          style="width: 35px; display: inline-block"
          v-model="value.expectedDiffPercent"
          :rules="ratioRules"
        ></v-text-field>
        % &nbsp; on &nbsp;
        <span style=" border-bottom: 1px solid" class="pb-2">
          {{leadingDevice}}
        </span>
      </div>

      <div v-if="calculatorEnabled">
        <h4>Sample size calculator</h4>

        Baseline value of {{ leadingMetricLabel }}:
        <span style="border-bottom: 1px solid; display:inline-block" class="pb-1 mt-1">
          {{value.leadingMetricBaselineValue}}
        </span>%
        <br/>

        Significance level (𝜶):
        <span style="border-bottom: 1px solid; display:inline-block" class="pb-1 mt-1">
          {{value.testAlpha}}
        </span>
        <br/>

        Test power:
        <span style="border-bottom: 1px solid; display:inline-block" class="pb-1 mt-1">
          {{value.testPower}}
        </span>
        <br/>

        Required sample size:
        <span style="display:inline-block" class="mt-2" v-if="!sendingDataToServer">
          <b>{{ requiredSampleSize }}</b>
        </span>
        <v-progress-circular indeterminate color="purple" v-if="sendingDataToServer"/>
        for each variant.
      </div>
    </v-card>
  </v-form>
</template>

<script>
  import { Record } from 'immutable'
  import { nonLegacyMetrics, getMetricByKey } from '../../model/experiment/metrics'
  import { formatError } from '../../model/errors'
  import { mapState, mapActions } from 'vuex'

  const ExperimentGoalEditingRecord = Record({
    hasHypothesis: false,
    leadingMetric: '',
    expectedDiffPercent: 0,
    testAlpha: 0,
    leadingMetricBaselineValue: 0,
    testPower: 0
  })

  export default {
    props: ['experiment', 'selectedDevice'],

    data () {
      const initialValue = this.init(this.experiment)
      return {
        givenValue: this.buildResult(initialValue),
        value: initialValue,
        formValid: true,
        metrics: nonLegacyMetrics(),
        ratioRules: [
          (v) => parseFloat(v).toString() === v || 'number!',
          (v) => v > 0 || 'c\'mon',
          (v) => v <= 100 || 'seriously?'
        ],
        sendingDataToServer: false,
        errors: []
      }
    },

    computed: {
      leadingDevice () {
        let device = this.selectedDevice || this.experiment.deviceClass

        if (!device || device === 'all') {
          return 'all devices'
        } else {
          return device
        }
      },

      calculatorEnabled () {
        return this.isCalculatorEnabled(this.value)
      },

      leadingMetricLabel () {
        return getMetricByKey(this.value.leadingMetric).label
      },

      hypothesisSwitchLabel () {
        if (this.value.hasHypothesis) {
          return 'I have the hypothesis:'
        }
        return 'I don\'h have hypothesis, I\'m feeling lucky.'
      },

      ...mapState({
        requiredSampleSize (state) {
          const sampleSize = state.calculateSampleSize.requiredSampleSize
          return sampleSize && sampleSize.toLocaleString('pl')
        }
      })
    },

    watch: {
      value: {
        handler: function (newValue) {
          if (this.validate()) {
            const result = this.buildResult(newValue)
            console.log('emit value ', result)

            this.calculate(result)
            this.$emit('input', result)
          }
        },
        deep: true
      }
    },

    methods: {
      init (experiment) {
        const value = {
          hasHypothesis: false,
          leadingMetric: 'tx_visit',
          expectedDiffPercent: '2',
          testAlpha: 0.05,
          leadingMetricBaselineValue: 5,
          testPower: 0.85
        }

        return value
      },

      buildResult (value) {
        let result = {
          hasHypothesis: value.hasHypothesis
        }

        if (value.hasHypothesis) {
          result.leadingMetric = value.leadingMetric
          result.expectedDiffPercent = parseFloat(value.expectedDiffPercent)

          if (this.isCalculatorEnabled(value)) {
            result.leadingMetricBaselineValue = value.leadingMetricBaselineValue
            result.testPower = value.testPower
            result.testAlpha = value.testAlpha
          }
        }

        return new ExperimentGoalEditingRecord(result)
      },

      isCalculatorEnabled (value) {
        return value &&
               value.hasHypothesis &&
               getMetricByKey(value.leadingMetric).isBinary
      },

      calculate (value) {
        if (!this.isCalculatorEnabled(value)) {
          return
        }

        this.prepareToSend()
        this.calculateSampleSize({
          params: {
            testAlpha: value.testAlpha,
            testPower: value.testPower,
            expectedDiffPercent: value.expectedDiffPercent,
            baselineMetricValue: value.leadingMetricBaselineValue
          }
        }).then(response => {
          console.log('received calculator query result:', response.data)
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
      },

      prepareToSend () {
        this.errors = []
        this.sendingDataToServer = true
      },

      afterSending () {
        this.sendingDataToServer = false
      },

      showError (error) {
          this.errors.push(formatError(error))
      },

      validate () {
        return this.$refs.goalEditingForm.validate()
      },

      ...mapActions([
        'calculateSampleSize'
      ])
    }
  }
</script>
