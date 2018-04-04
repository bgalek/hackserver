<template>
  <v-form>
    <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
      <v-layout row align-center>

        <v-flex offset-xs1>
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
          <v-slider
            v-model="value.percentage"
            thumb-label
            :label="parseInt(value.percentage) + '% per variant'"
            step="1"
            v-bind:max="maxPercentPerVariant"
            v-bind:min="1"
            ticks></v-slider>
        </v-flex>

      </v-layout>

      <v-layout row align-center>

        <v-flex offset-xs1>
          <v-select
            label="Variants"
            :rules="variantsRules"
            chips
            append-icon=""
            tags
            v-model="value.variantNames">
            <template slot="selection" slot-scope="data">

              <v-chip
                v-if="data.item === baseVariant"
                :selected="data.selected"
                :disabled="true">
                <strong>{{ data.item }}</strong>&nbsp;
              </v-chip>

              <v-chip
                v-else
                close
                @input="removeVariant(data.item)"
                :selected="data.selected">
                <strong>{{ slugify(data.item) }}</strong>&nbsp;
              </v-chip>

            </template>
          </v-select>
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
            v-model="value.internalVariantName"
            label="Internal variant"
          ></v-text-field>
        </v-flex>

      </v-layout>


    </v-container>

  </v-form>
</template>

<script>
  import {isUri} from 'valid-url'

  export default {
    props: ['variants'],

    data () {
      const baseVariant = 'base'

      console.log('this', this)
      return {
        value: this.init(this.variants),
        deviceClasses: ['all', 'phone', 'desktop', 'tablet'],
        baseVariant: baseVariant,
        variantsRules: [
          (v) => this.variantsUnique() || 'Slugified variant names must be unique.',
          (v) => this.slugifiedVariants.indexOf('') === -1 || 'Slugified variant name can not be empty.',
          (v) => this.noOfVariants() > 1 || 'No variants. Seriously?'
        ],
      }
    },

    methods: {
      init (variants) {
        console.log('variants', variants)
        const value = {
          variantNames: variants && Array.from(variants.variantNames) || ['base'],
          percentage: variants && variants.percentage || 1,
          internalVariantName: variants && variants.internalVariantName,
          deviceClass: variants && variants.deviceClass || 'all'
        }
        console.log('value', value)
        this.$emit('input', value)
        return value
      },

      inputEntered () {
        this.$emit('input', this.value)
      },

      isUrlValid (value) {
        return isUri(value) !== undefined || !value
      },

      removeVariant (variant) {
        this.remove('variantNames', variant)
      },

      remove (arrayName, toRemove) {
        this[arrayName].splice(this[arrayName].indexOf(toRemove), 1)
        this[arrayName] = [...this[arrayName]]
      },

      slugify (str) {
        return str.toString().toLowerCase()
          .replace(/\s+/g, '-')
          .replace(/[^\w-]+/g, '')
          .replace(/--+/g, '-')
          .replace(/^-+/, '')
          .replace(/-+$/, '')
      },
    },

    computed: {
      maxPercentPerVariant () {
        return 100 / this.value.variantNames.length
      }
    }
  }
</script>
