<template>
  <v-form ref="addToGroupEditingForm" v-model="formValid">
  <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
  <v-layout row align-center>

  <v-flex xs1>
    <v-tooltip right close-delay="1000">
      <span>
        Experiment Draft can join an existing group or can init a new one.<br/>
        Started experiment (Active or Paused) can only init a new group.<br/>
        <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/#grupa-eksperymentow">
        Read more about groups in χ Docs</a>
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
  </v-layout>

  <v-layout>
    <v-flex>
      <v-btn flat @click="closeAddToGroup()">Cancel</v-btn>
    </v-flex>

    <v-flex>
      <v-btn color="gray"
             @click="addToGroup()"
             :disabled="!groupNameChanged() || !this.formValid"
             style="text-transform: none">
        Add experiment &nbsp; <b>{{ this.experiment.id }}</b>&nbsp; to group &nbsp; <b>{{ this.slugifiedValue}}</b>
      </v-btn>
    </v-flex>

  </v-layout>

  </v-container>
  </v-form>
</template>

<script>
  import { slugify } from '../../utils/slugify'
  import _ from 'lodash'

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
          (v) => this.isExperimentGroupIdUnique(v) || 'Experiment group ID must be unique'
        ]
      }
    },

    computed: {
      givenGroup: function () {
        return this.experiment.experimentGroup
      },

      slugifiedValue: function () {
        return slugify(this.value)
      }
    },

    methods: {
      isExperimentGroupIdUnique (name) {
        return _.find(this.experimentGroupNames, e => e === name) === undefined
      },

      validate () {
        return this.$refs.addToGroupEditingForm.validate()
      },

      closeAddToGroup () {
        this.value = this.givenGroup
        this.$emit('closeAddToGroup')
      },

      addToGroup () {
        this.$emit('addToGroup', this.slugifiedValue)
      },

      groupNameChanged () {
        return this.slugifiedValue !== this.givenGroup
      }
    }
  }
</script>
