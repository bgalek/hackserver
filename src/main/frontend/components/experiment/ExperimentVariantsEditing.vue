<template>
  <v-form ref="variantsEditingForm" v-model="formValid">

    <v-container fluid class="pa-0 ma-0">

      <v-layout row v-if="showHeader">
        <v-flex offset-xs1>
          <h3 class="mt-3">Variants</h3>
        </v-flex>
      </v-layout>

      <v-layout row text-xs-center align-center>
        <v-flex xs1 >
          <v-tooltip top>
            <ul>
              <li>all = any kind of device</li>
              <li>desktop = classic web browser on desktop or laptop</li>
              <li>phone = mobile web browser</li>
              <li>mobilapp = native mobile app (for buyers) including mobile-opbox</li>
            </ul>
            <v-icon
              slot="activator">help_outline</v-icon>
          </v-tooltip>
        </v-flex>
        <v-flex xs11 lg6 id="deviceClassDropdown">
            <v-select
            v-bind:items="deviceClasses"
            v-model="value.deviceClass"
            label="Device class"
            item-value="text"
           ></v-select>
        </v-flex>

      </v-layout>
      <v-layout row text-xs-center align-center>

        <v-flex offset-xs1 lg6>
          <v-combobox
            multiple
            id="experimentVariants"
            label="Variants"
            :rules="variantsRules"
            :readonly="!allowModifyRegularVariants"
            chips
            append-icon=""
            v-model="value.variantNames">
            <template slot="selection" slot-scope="data">

              <v-chip
                v-if="allowDeleteVariant(data.item)"
                :selected="data.selected"
                :disabled="true">
                <strong>{{ data.item }}</strong>&nbsp;
              </v-chip>

              <v-chip
                v-else
                close
                @input="removeVariantName(data.item)"
                :selected="data.selected">
                <strong>{{ slugify(data.item) }}</strong>&nbsp;
              </v-chip>

            </template>
          </v-combobox>
        </v-flex>

      </v-layout>
      <v-layout row align-center text-xs-center>

        <v-flex xs1>
          <v-tooltip top>
            <span>
              Percent of traffic eligible for experiments to be assigned for each variant.
              <br/>
              Remember that large part of our traffic is not eligible for experiments,
              for example:
              <ul>
                <li>anonymous traffic without cmuid</li>
                <li>cached traffic</li>
              </ul>

            </span>
            <v-icon
              slot="activator">help_outline</v-icon>
          </v-tooltip>
        </v-flex>
        <v-flex xs11 lg6>
          <v-slider
            id="percentageVariantSlider"
            v-model="value.percentage"
            thumb-label
            :label="parseInt(value.percentage) + '% per variant'"
            step="1"
            v-bind:max="computeMaxPercentagePerVariant()"
            v-bind:min="1"
            :disabled="blockPercentageSlider"
            ticks></v-slider>
        </v-flex>

      </v-layout>
      <v-layout row align-center text-xs-center>

        <v-flex xs1>
          <v-tooltip right>
            <span><b>Internal</b> means &mdash; available only in Allegro intranet</span>
            <v-icon
              slot="activator">help_outline</v-icon>
          </v-tooltip>
        </v-flex>
        <v-flex xs11 lg6>
          <v-text-field
            id="internalVariantFormField"
            :rules="internalVariantNameRules"
            v-model="value.internalVariantName"
            @input="onInternalVariantNameChange"
            label="Internal variant"
          ></v-text-field>
        </v-flex>

      </v-layout>

      <v-layout row align-center>

        <v-flex offset-xs1>
          <v-text-field
            id="internalVariantNameSlug"
            style="width: 300px"
            v-model="value.slugifiedInternalVariantName"
            label="Final internal variant name"
            :readonly="true"
            :disabled="true"
          ></v-text-field>
        </v-flex>

      </v-layout>

      <v-layout row align-center v-if="showButtons">
        <v-flex>
          <v-btn flat @click="closeVariants()">Cancel</v-btn>
        </v-flex>

        <v-flex>
        <v-btn color="gray"
               :disabled="!variantsChanged() || !this.formValid"
               @click="updateVariants"
               style="text-transform: none">
          Update variants of &nbsp;<b>{{ this.experiment.id }}</b>
        </v-btn>
        </v-flex>
      </v-layout>

    </v-container>

  </v-form>
</template>

<script>
  import _ from 'lodash'
  import { slugify } from '../../utils/slugify'
  import { startsOrEndsWithSpace } from '../../utils/startsOrEndsWithSpace'
  import { Record, List } from 'immutable'

  const ExperimentVariantsEditingRecord = Record({
    variantNames: List(),
    percentage: 0,
    internalVariantName: '',
    deviceClass: ''
  })

  export default {
    props: {
      experiment: {},
      showButtons: false,
      allowModifyRegularVariants: {
        default: false,
        type: Boolean
      },
      showHeader: false
    },

    data () {
      const initialValue = this.init(this.experiment)
      return {
        givenValue: this.buildResult(initialValue),
        value: initialValue,
        formValid: true,
        deviceClasses: [
          'all',
          'desktop',
          'phone',
          'phone-android',
          'phone-iphone',
          'mobileapp',
          'mobileapp-android',
          'mobileapp-iphone'
        ],
        baseVariant: 'base',
        variantsRules: [
          (v) => this.baseVariantPresent() || 'base variant is mandatory',
          (v) => this.variantsUnique() || 'Slugified variant names must be unique.',
          (v) => this.slugifiedVariants.indexOf('') === -1 || 'Slugified variant name can not be empty.',
          (v) => this.noOfVariants() > 1 || 'No variants. Seriously?'
        ],
        internalVariantNameRules: [
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end variant name with space'
        ],
        blockPercentageSlider: this.shouldBlockPercentageSlider(initialValue)
      }
    },

    methods: {
      init (experiment) {
        const value = {
          variantNames: (experiment && Array.from(experiment.variantNames)) || ['base'],
          percentage: (experiment && experiment.percentage) || 1,
          internalVariantName: experiment && experiment.internalVariantName,
          slugifiedInternalVariantName: experiment && slugify(experiment.internalVariantName),
          deviceClass: (experiment && experiment.deviceClass) || 'all'
        }
        this.$emit('input', this.buildResult(value))
        return value
      },

      validate () {
        return this.$refs.variantsEditingForm.validate()
      },

      buildResult (value) {
        return new ExperimentVariantsEditingRecord({
          variantNames: value.variantNames,
          percentage: value.percentage,
          internalVariantName: slugify(value.internalVariantName),
          deviceClass: value.deviceClass
        })
      },

      buildResultFromValue () {
        return this.buildResult(this.value)
      },

      shouldBlockPercentageSlider (value) {
        if (!value) {
          return true
        }
        return value.variantNames.length <= 1
      },

      variantsUnique () {
        let distinctVariants = new Set(this.slugifiedVariants)
        return (this.slugifiedVariants.length === distinctVariants.size)
      },

      removeVariant (variant) {
        this.remove('variantNames', variant)
      },

      removeVariantName (variantName) {
        const i = this.value.variantNames.indexOf(variantName)
        this.value.variantNames.splice(i, 1)
      },

      remove (arrayName, toRemove) {
        this[arrayName].splice(this[arrayName].indexOf(toRemove), 1)
        this[arrayName] = [...this[arrayName]]
      },

      baseVariantPresent () {
        return this.value.variantNames.indexOf('base') !== -1
      },

      noOfVariants () {
        return this.slugifiedVariants.length + (this.internalVariantSet() ? 1 : 0)
      },

      internalVariantSet () {
        const slugified = slugify(this.value.internalVariantName)
        return !!slugified && slugified !== ''
      },

      allowDeleteVariant (variantName) {
        return variantName === this.baseVariant || this.allowModifyRegularVariants === false
      },

      onInternalVariantNameChange (event) {
        this.value.slugifiedInternalVariantName = slugify(event)
      },

      computeMaxPercentagePerVariant () {
        if (this.allowModifyRegularVariants) {
          if (!this.value.variantNames) {
            return 100
          }
          return Math.floor(100 / this.value.variantNames.length)
        } else {
          return this.experiment.maxPossibleAllocation
        }
      },

      updateVariants () {
        const newValue = this.buildResultFromValue()
        this.givenValue = newValue
        this.$emit('updateVariants', newValue)
      },

      closeVariants () {
        this.value = this.init(this.experiment)
        this.$emit('closeVariants')
      },

      variantsChanged () {
        return !this.givenValue.equals(this.buildResultFromValue())
      },

      slugify (value) {
        return slugify(value)
      }
    },

    watch: {
      value: {
        handler (newValue) {
          this.blockPercentageSlider = this.shouldBlockPercentageSlider(newValue)

          if (this.validate()) {
            this.$emit('input', this.buildResultFromValue())
          }
        },

        deep: true
      }
    },

    computed: {
      slugifiedVariants () {
        return _.map(this.value.variantNames, v => slugify(v))
      }
    }
  }
</script>


