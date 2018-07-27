<template>
  <v-form>
    <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>

      <v-layout row align-center>

        <v-flex xs1>
          <v-tooltip right>
            <span>
              Custom parameter
              <br/>
              When defined, then passing the same parameter will be required to assign a user to this experiment
               <br/>
              <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/pl/latest/">
              Read more in χ Docs</a>

            </span>
            <v-icon
              slot="activator">help_outline
            </v-icon>
          </v-tooltip>
        </v-flex>

        <v-flex xs11>
          <v-switch
            label="Define custom parameter"
            v-model="defineCustomParameter"
            v-on:change="onDefineCustomParameterChange"
          ></v-switch>
          <v-text-field
            v-if="defineCustomParameter"
            id="experimentCustomParameterNameFormField"
            v-model="value.name"
            :rules="customParameterNameRules"
            label="Custom parameter name"
            v-on:input="inputEntered()">
            >
          </v-text-field>
          <v-text-field
            v-if="defineCustomParameter"
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

    data() {
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

    methods: {
      init(experiment) {
        const value = {
          name: null,
          value: null
        }

        this.$emit('input', value)
        return value
      },

      inputEntered() {
        this.$emit('input', this.value)
      },

      onDefineCustomParameterChange(newVal) {
        if (!newVal) {
          this.value.name = null
          this.value.value = null
          this.$emit('input', this.value)
        }
      }
    }
  }
</script>
