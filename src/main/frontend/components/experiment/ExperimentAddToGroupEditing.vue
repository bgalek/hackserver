<template>
  <v-form>
  <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
  <v-layout row align-center>

  <v-flex xs1>
    <v-tooltip right close-delay="1000">
      <span>
        You can create a group of mutually exclusive experiments.<br/>
        Experiments in a group will not intersect with each other.<br/>
        Useful when you have many experiments in the same area of the system.<br/>
        <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/#grupa-eksperymentow">
        Read more in χ Docs</a>
      </span>
      <v-icon
        slot="activator">help_outline</v-icon>
    </v-tooltip>
  </v-flex>
  <v-flex xs11>
    <v-combobox
      v-if="experiment.canJoinAnyGroup()"
      label="Select an existing group or create a new one"
      v-model="value"
      :items="experimentGroupNames"
      :rules="groupNameRules"
    ></v-combobox>

    <v-text-field
      v-if="!experiment.canJoinAnyGroup()"
      v-model="value"
      label="Name of a new group"
      :rules="createGroupRules"
    ></v-text-field>
  </v-flex>

   {{ this.experimentGroupNames }}
  </v-layout>
  </v-container>
  </v-form>
</template>

<script>
  import { slugify } from '../../utils/slugify'

  export default {
    props: ['experiment', 'experimentGroupNames'],

    data () {
      return {
        formValid: true,
        value: null,
        groupNameRules: [
          (v) => !!v || 'Group name can not be empty'
        ],
        createGroupRules: [
          (v) => !!v || 'Group name can not be empty',
          (v) => this.isExperimentGroupIdUnique(v) || 'Experiment group ID must be unique',
        ]
      }
    },

    methods: {
      isExperimentGroupIdUnique(name) {
        return _.find(this.experimentGroupNames, e => e === name) === undefined
      }
    },

    watch: {
      value (value) {
        this.$emit('input', slugify(value))
      }
    }
  }
</script>
