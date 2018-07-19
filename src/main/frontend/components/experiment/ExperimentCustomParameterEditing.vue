<template>
  <v-form>
    <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
      <v-layout row align-center>

        <v-flex xs1>
          <v-tooltip right>
            <span>(optional) Define custom parameter</span>
            <v-icon
              slot="activator">help_outline
            </v-icon>
          </v-tooltip>
        </v-flex>
        <v-flex xs11>

          <v-text-field
            id="experimentCustomParameterNameFormField"
            v-model="value.name"
            :rules="customParameterNameRules"
            label="Custom parameter name"
            v-on:input="inputEntered()">
            >
          </v-text-field>
        </v-flex>

      </v-layout>
      <v-layout row align-center>

        <v-flex offset-xs1>
          <v-text-field
            id="experimentCustomParameterValueFormField"
            v-model="value.value"
            :rules="customParameterValueRules"
            label="Custom parameter value"
            v-on:input="inputEntered()"
          ></v-text-field>
        </v-flex>

      </v-layout>
    </v-container>

  </v-form>
</template>


<script>
  import {startsOrEndsWithSpace} from '../../utils/startsOrEndsWithSpace'

  export default {
    props: ['experiment'],

    data () {
      let value1 = this.init(this.experiment)

      return {
        value: value1,

        customParameterNameRules: [
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end string with space',
          (v) => !!v || this.isCustomParameterDefined() || 'Enter name or remove custom parameter value'
        ],
        customParameterValueRules: [
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end string with space',
          (v) => !!v || this.isCustomParameterDefined() || 'Enter value or remove custom parameter name'
        ]
      }
    },

    methods: {
      init (experiment) {
        const value = {
          name: experiment && experiment.customParam.name,
          value: experiment && experiment.customParam.value
        }

        this.$emit('input', value)
        return value
      },

      inputEntered () {
        this.$emit('input', this.value)
      },

      isCustomParameterDefined () {
        return !(!!this.value.name || !!this.value.value)
      }
    }
  }
</script>
