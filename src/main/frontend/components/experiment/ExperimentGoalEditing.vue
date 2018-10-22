<template>
  <v-form ref="goalEditingForm" v-model="formValid">

    <v-card  class="pa-3">
      <h3 class="pa-0">Experiments goal</h3>

      <v-radio-group v-model="value.hasHypothesis">
        <v-radio label="I'm feeling lucky" :value="false"/>
        <v-radio label="I have hypothesis" :value="true"/>
      </v-radio-group>

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
          :rules="expectedDiffRules"
        ></v-text-field>
        % &nbsp; on &nbsp;
        <span style=" border-bottom: 1px solid" class="pb-2">
          {{leadingDevice}}
        </span>
      </div>

      <div v-if="value.hasHypothesis && isBinaryMetricSelected">
        Calculator :o
      </div>
    </v-card>

  </v-form>
</template>

<script>
  import { Record } from 'immutable'
  import {nonLegacyMetrics, getMetricByKey} from '../../model/experiment/metrics'

  const ExperimentGoalEditingRecord = Record({
    hasHypothesis: false,
    leadingMetric: '',
    expectedDiffPercent: ''
  })

  export default {
    props: ['experiment', 'selectedDevice'],

    data () {
      console.log("nonLegacyMetrics()", nonLegacyMetrics())
      const initialValue = this.init(this.experiment)
      return {
        givenValue: this.buildResult(initialValue),
        value: initialValue,
        formValid: true,
        metrics: nonLegacyMetrics(),
        expectedDiffRules: [
          (v) => parseInt(v).toString() === v || 'integer!',
          (v) => v > 0 || 'c\'mon',
          (v) => v <= 100 || 'seriously?'
        ],
      }
    },

    computed: {
      leadingDevice: function () {
        let device = this.selectedDevice || this.experiment.deviceClass

        if (!device || device == 'all') {
          return 'all devices'
        }
        else {
          return device
        }
      },

      isBinaryMetricSelected: function () {
        return this.value && getMetricByKey(this.value.leadingMetric).isBinary
      }
    },

    watch: {
      value: {
        handler: function (newValue) {
          if (this.validate()) {
            console.log('emit value ', this.buildResult(newValue))
            this.$emit('input', this.buildResult(newValue))
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
          expectedDiffPercent: '2'
        }

        return value
      },

      buildResult (value) {
        return new ExperimentGoalEditingRecord({
          hasHypothesis: value.hasHypothesis,
          leadingMetric: value.leadingMetric,
          expectedDiffPercent: parseInt(value.expectedDiffPercent)
        })
      },

      validate () {
        return this.$refs.goalEditingForm.validate()
      }
    }
  }
</script>
