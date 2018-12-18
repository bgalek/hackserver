<template>
  <v-form ref="goalEditingForm" v-model="formValid">

    <v-container fluid class="pa-0 ma-0">

    <v-layout row v-if="showHeader">
      <v-flex offset-xs1>
        <h3 class="mt-3 blue--text">Experiment goal</h3>
      </v-flex>
    </v-layout>

    <v-alert v-for="error in errors"
             color="error" icon="warning" value="true" :key="error">
      {{ error }}
    </v-alert>

    <v-layout row>
      <v-flex offset-xs1>
        <v-switch :label="hypothesisSwitchLabel"
                  v-model="value.hasHypothesis"
                  color="orange darken-3"
                  class="pa-0">
        </v-switch>
      </v-flex>
    </v-layout>

    <v-layout row v-if="value.hasHypothesis">
      <v-flex offset-xs1>
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
          % &nbsp; on {{leadingDevice}}
      </v-flex>
    </v-layout>

    <v-layout row v-if="calculatorEnabled">
      <v-flex offset-xs1 lg6>
        <v-expansion-panel style="-webkit-box-shadow: 0px 0px; box-shadow: 0px 0px;">
          <v-expansion-panel-content>

            <div slot="header">
              <b>Sample size calculator</b>
            </div>

            <v-card>

              <div>
                <v-tooltip top>
                  This is just a rough estimation.
                  Chi will adjust this parameter after first day of the experiment.
                  <v-icon slot="activator">help_outline</v-icon>  &nbsp;
                </v-tooltip>

                Baseline value of {{ leadingMetricLabel }}: &nbsp;

                <v-text-field class="pa-0 ma-0"
                            style="width: 35px; display: inline-block"
                            v-model="value.leadingMetricBaselineValue"
                            :rules="ratioRules"
                ></v-text-field>%
              </div>

              <div>
                <v-tooltip top>
                  Risk of obtaining false positive results &mdash;
                  finding an non existing difference.

                  <v-icon slot="activator">help_outline</v-icon>  &nbsp;
                </v-tooltip>

                Significance level (𝜶): &nbsp;

                <v-select class="pa-0"
                          style="width: 35px; display: inline-block"
                          v-bind:items="alphaLevels"
                          v-model="value.testAlpha"
                ></v-select>
              </div>

              <div>
                <v-tooltip top>
                  Chance of obtaining true positive results &mdash;
                  finding a difference when it really exists.
                  <v-icon slot="activator">help_outline</v-icon>  &nbsp;
                </v-tooltip>

                Test power: &nbsp;

                <v-select class="pa-0"
                          style="width: 35px; display: inline-block"
                          v-bind:items="powerLevels"
                          v-model="value.testPower"
                ></v-select>
              </div>
            </v-card>
          </v-expansion-panel-content>
        </v-expansion-panel>

        <div>
          Required sample size:
          <span style="display:inline-block" class="mt-2" v-if="!sendingDataToServer">
               <b>{{ value.requiredSampleSize }}</b>
              </span>
          <v-progress-circular indeterminate color="purple" v-if="sendingDataToServer"/>
          for each variant.
        </div>

      </v-flex>
    </v-layout>

     </v-container>
  </v-form>
</template>

<script>
  import { Record } from 'immutable'
  import { nonLegacyMetrics, getMetricByKey, nonLegacyMetricsWithoutCustomMetric } from '../../model/experiment/metrics'
  import { formatError } from '../../model/errors'
  import { mapActions } from 'vuex'

  const ExperimentGoalEditingRecord = Record({
    leadingMetric: '',
    expectedDiffPercent: 0,
    testAlpha: 0,
    leadingMetricBaselineValue: 0,
    testPower: 0,
    requiredSampleSize: 0
  })

  export default {
    props: ['experiment', 'selectedDevice', 'showHeader', 'haveCustomMetric'],

    data () {
      const initialValue = this.init(this.experiment)
      return {
        givenValue: this.buildResult(initialValue),
        value: initialValue,
        formValid: true,
        metrics: nonLegacyMetricsWithoutCustomMetric(),
        alphaLevels: [0.01, 0.05, 0.10],
        powerLevels: [0.80, 0.85, 0.90, 0.95],
        ratioRules: [
          (v) => this.isNumber(v) || 'number!',
          (v) => v > 0 || 'c\'mon',
          (v) => v < 100 || 'seriously?'
        ],
        sendingDataToServer: false,
        errors: []
      }
    },

    computed: {
      leadingDevice () {
        let device = this.selectedDevice
        if (this.experiment && this.experiment.deviceClass) {
          device = this.experiment.deviceClass
        }

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
        return 'I don\'t have any hypothesis, I\'m feeling lucky.'
      }
    },

    watch: {
      value: {
        handler: function (newValue) {
          if (this.validate()) {
            const result = this.buildResult(newValue)
            if (this.validate()) {
              this.calculate(newValue)
            }
            this.$emit('input', result)
          }
        },
        deep: true
      },
      haveCustomMetric: {
        handler: function (isCustomMetricVisible) {
          this.metrics = isCustomMetricVisible ? nonLegacyMetrics() : nonLegacyMetricsWithoutCustomMetric()
        }
      }
    },

    methods: {
      init (experiment) {
        const value = {
          hasHypothesis: true,
          leadingMetric: 'tx_visit',
          expectedDiffPercent: 2.5,
          testAlpha: 0.05,
          leadingMetricBaselineValue: 5,
          testPower: 0.85,
          requiredSampleSize: 0
        }

        this.calculate(value)
        return value
      },

      buildResult (value) {
        let result = {}

        if (value.hasHypothesis) {
          result.leadingMetric = value.leadingMetric
          result.expectedDiffPercent = parseFloat(value.expectedDiffPercent)

          if (this.isCalculatorEnabled(value)) {
            result.leadingMetricBaselineValue = parseFloat(value.leadingMetricBaselineValue)
            result.testPower = value.testPower
            result.testAlpha = value.testAlpha
            result.requiredSampleSize = value.requiredSampleSize
          }
        }

        return new ExperimentGoalEditingRecord(result)
      },

      isCalculatorEnabled (value) {
        return value &&
               value.hasHypothesis &&
               value.leadingMetric &&
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
          this.value.requiredSampleSize = response.data
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
      ]),

      isNumber (v) {
        if (typeof v === 'number') {
          return true
        }
        return !isNaN(v)
      }
    }
  }
</script>
