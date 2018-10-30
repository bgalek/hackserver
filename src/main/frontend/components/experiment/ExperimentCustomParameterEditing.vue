<template>
  <v-form ref="customParameterForm">
    <v-container fluid class="pa-0 ma-0">

      <v-layout row v-if="showHeader">
        <v-flex offset-xs1>
          <h3 class="mt-3">Custom parameter</h3>
        </v-flex>
      </v-layout>

      <v-layout row text-xs-center align-center>

        <v-flex xs1 right>
          <v-tooltip top>
            <span>
              Custom parameter
              <br/>
              When defined, then passing the same parameter will be required to assign a user to this experiment
               <br/>
              <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/">
              Read more in χ Docs</a>
            </span>
            <v-icon slot="activator">help_outline</v-icon>
          </v-tooltip>
        </v-flex>

        <v-flex xs11>
          <v-switch
            label="Define custom parameter"
            v-model="defineCustomParameter"
            v-on:change="onDefineCustomParameterChange"
          ></v-switch>
        </v-flex>
      </v-layout>

      <v-layout row v-if="defineCustomParameter">
          <v-flex offset-xs1 lg6>
          <v-text-field
            id="experimentCustomParameterNameFormField"
            v-model="value.name"
            :rules="customParameterNameRules"
            label="Custom parameter name"
          ></v-text-field>
          <v-text-field
            v-if="defineCustomParameter"
            id="experimentCustomParameterValueFormField"
            v-model="value.value"
            :rules="customParameterValueRules"
            label="Custom parameter value"
          ></v-text-field>
          </v-flex>
      </v-layout>
    </v-container>
  </v-form>

</template>


<script>
  import {startsOrEndsWithSpace} from '../../utils/startsOrEndsWithSpace'
  import { slugify } from '../../utils/slugify'
  import { Record } from 'immutable'

  const CustomParamEditingRecord = Record({
    name: null,
    value: null
  })

  export default {
    props: ['experiment', 'showHeader'],

    data () {
      return {
        value: this.init(this.experiment),
        customParameterNameRules: [
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end string with space',
          (v) => !!this.value.name || 'Enter custom parameter name'
        ],
        customParameterValueRules: [
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end string with space',
          (v) => !!this.value.value || 'Enter custom parameter value'
        ],
        defineCustomParameter: false
      }
    },

    watch: {
      value: {
        handler: function (newValue) {
          if (this.validate()) {
            this.$emit('input', this.buildResultFromValue())
          }
        },
        deep: true
      }
    },

    methods: {
      init (experiment) {
        return {
          name: null,
          value: null
        }
      },

      onDefineCustomParameterChange (newVal) {
        if (!newVal) {
          this.$emit('input', this.buildResult({}))
        }
      },

      buildResult (value) {
        return new CustomParamEditingRecord({
          name: slugify(value.name),
          value: slugify(value.value)
        })
      },

      buildResultFromValue () {
        return this.buildResult(this.value)
      },

      validate () {
        return this.$refs.customParameterForm.validate()
      }
    }
  }
</script>
