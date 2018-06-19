<template>
  <v-form>
    <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
      <v-layout row align-center>

        <v-flex offset-xs1 id="deviceClassDropdown">
          <v-select
            v-bind:items="deviceClasses"
            v-model="value.deviceClass"
            label="Device class"
            item-value="text"
          ></v-select>
        </v-flex>

      </v-layout>

      <v-layout row align-center>

        <v-flex offset-xs1>
          <v-select
            id="experimentVariants"
            label="Variants"
            :rules="variantsRules"
            :readonly="!allowModifyRegularVariants"
            chips
            append-icon=""
            tags
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
          </v-select>
        </v-flex>

      </v-layout>

      <v-layout row align-center>

        <v-flex offset-xs1>
          <v-slider
            id="percentageVariantSlider"
            v-model="value.percentage"
            thumb-label
            :label="parseInt(value.percentage) + '% per variant'"
            step="1"
            v-bind:max="maxPercentPerVariant"
            v-bind:min="1"
            :disabled="blockPercentageSlider"
            ticks></v-slider>
        </v-flex>

      </v-layout>

      <v-layout row align-center>

        <v-flex xs1>
          <v-tooltip right>
            <span><b>Internal</b> means &mdash; available only in Allegro intranet.</span>
            <v-icon
              slot="activator">help_outline</v-icon>
          </v-tooltip>
        </v-flex>
        <v-flex xs11>
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

    </v-container>

  </v-form>
</template>

<script>
  import _ from 'lodash'
  import { slugify } from '../../utils/slugify'
  import { startsOrEndsWithSpace } from '../../utils/startsOrEndsWithSpace'

  export default {
    props: {
      experimentToPair: {},

      experiment: {},

      allowModifyRegularVariants: {
        default: false,
        type: Boolean
      }
    },

    data () {
      const baseVariant = 'base'
      const initialValue = this.init(this.experiment)
      return {
        value: initialValue,
        deviceClasses: ['all', 'phone', 'desktop', 'tablet'],
        baseVariant: baseVariant,
        variantsRules: [
          (v) => this.baseVariantPresent() || 'base variant is mandatory',
          (v) => this.variantsUnique() || 'Slugified variant names must be unique.',
          (v) => this.slugifiedVariants.indexOf('') === -1 || 'Slugified variant name can not be empty.',
          (v) => this.noOfVariants() > 1 || 'No variants. Seriously?'
        ],
        internalVariantNameRules: [
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end variant name with space'
        ],
        maxPercentPerVariant: 100,
        blockPercentageSlider: this.shouldBlockPercentageSlider(initialValue)
      }
    },

    methods: {
      init (experiment) {
        const value = {
          variantNames: (experiment && Array.from(experiment.variantNames)) || ['base'],
          percentage: (experiment && experiment.percentage) || 1,
          internalVariantName: experiment && experiment.internalVariantName,
          slugifiedInternalVariantName: experiment && this.slugify(experiment.internalVariantName),
          deviceClass: (experiment && experiment.deviceClass) || 'all'
        }
        this.$emit('input', value)
        return value
      },

      shouldBlockPercentageSlider (value) {
        if (!value) {
          return true
        }
        return value.variantNames.length <= 1
      },

      inputEntered () {
        this.$emit('input', this.value)
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
        this.value.variantNames = this.value.variantNames.splice(i, 1)
        this.inputEntered()
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
        const slugified = this.slugify(this.value.internalVariantName)
        return !!slugified && slugified !== ''
      },

      allowDeleteVariant (variantName) {
        return variantName === this.baseVariant || this.allowModifyRegularVariants === false
      },

      slugify (str) {
        return slugify(str)
      },

      onInternalVariantNameChange (event) {
        this.value.slugifiedInternalVariantName = this.slugify(event)
      },

      computeMaxPercentagePerVariant (experimentToPair, numberOfVariants) {
        if (experimentToPair) {
          let percentageLeft = 100 - (experimentToPair.variants.length - 1) * experimentToPair.percentage
          if (percentageLeft / numberOfVariants >= experimentToPair.percentage) {
            return percentageLeft / numberOfVariants
          } else {
            return (100 - (experimentToPair.variants.length) * experimentToPair.percentage) / (numberOfVariants - 1)
          }
        } else {
          return 100 / numberOfVariants
        }
      }
    },

    watch: {
      experimentToPair (newExperimentToPair) {
        this.maxPercentPerVariant = Math.floor(this.computeMaxPercentagePerVariant(newExperimentToPair, this.value.variantNames.length))
      },

      value: {
        handler (newValue) {
          this.maxPercentPerVariant = Math.floor(this.computeMaxPercentagePerVariant(this.experimentToPair, newValue.variantNames.length))
          this.blockPercentageSlider = this.shouldBlockPercentageSlider(newValue)
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
