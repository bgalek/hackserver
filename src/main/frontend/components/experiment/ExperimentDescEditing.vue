<template>
  <v-form ref="descEditingForm" v-model="formValid">

    <v-container fluid class="pa-0 ma-0" text-xs-center>
      <v-layout row align-center>

        <v-flex xs1>
          <v-tooltip right>
            <span>Describe shortly what you are going to test.</span>
            <v-icon
              slot="activator">help_outline</v-icon>
          </v-tooltip>
        </v-flex>
        <v-flex xs11 lg10>
          <v-text-field
            id="experimentDescriptionFormField"
            v-model="value.description"
            :rules="stringsRules"
            label="Description">
          ></v-text-field>
        </v-flex>

      </v-layout>
      <v-layout top align-center>

        <v-flex offset-xs1 lg10>
          <v-text-field
            id="experimentDocumentationLinkFormField"
            v-model="value.documentLink"
            :rules="documentLinkRules"
            label="Documentation link"
          ></v-text-field>
        </v-flex>

      </v-layout>

      <v-layout row align-center>
          <v-flex xs1>
            <v-tooltip right close-delay="1000">
              <span>
                Let your team manage this experiment.
                Provide a group name, for example: <code>Tech Content Team</code>.
                <br/>
                Hit <b>ENTER</b> to add a group.
              </span>
              You can add more than one group.
              <br/>
              Check your groups on your JIRA profile<br/>
              <a target="_blank" style="color: aqua" href="https://jira.allegrogroup.com/secure/ViewProfile.jspa">
                https://jira.allegrogroup.com/secure/ViewProfile.jspa</a>
              <v-icon
                slot="activator">help_outline</v-icon>
            </v-tooltip>
          </v-flex>

          <v-flex xs11 lg6>
            <v-combobox
              multiple
              id="experimentAuthorizationGroupsFormField"
              label="Authorization groups"
              slot="activator"
              chips
              append-icon=""
              v-model="value.groups"
              :rules="groupsRules">
              <template slot="selection" slot-scope="data">
                <v-chip
                  close
                  @input="removeGroup(data.item)"
                  :selected="data.selected">
                  <strong>{{ data.item }}</strong>&nbsp;
                </v-chip>
              </template>
            </v-combobox>
          </v-flex>
      </v-layout>

      <v-layout row align-center v-if="showButtons">
          <v-flex>
            <v-btn flat
                   @click="closeDescriptions()">
              Cancel
            </v-btn>
          </v-flex>

          <v-flex>
          <v-btn  color="gray"
                 :disabled="!descriptionsChanged() || !this.formValid"
                 @click="updateDescriptions"
                 style="text-transform: none">
            Update descriptions of &nbsp;<b>{{ this.getExpId() }}</b>
          </v-btn>
          </v-flex>
      </v-layout>

    </v-container>

  </v-form>
</template>

<script>
  import { isUri } from 'valid-url'
  import { startsOrEndsWithSpace } from '../../utils/startsOrEndsWithSpace'
  import { Record, List } from 'immutable'

  const ExperimentDescEditingRecord = Record({
    documentLink: null,
    description: null,
    groups: List()
  })

  export default {
    props: ['experiment', 'showButtons'],

    data () {
      const initialValue = this.init(this.experiment)
      return {
        givenValue: this.buildResult(initialValue),
        value: initialValue,
        formValid: true,
        documentLinkRules: [
          (v) => this.isUrlValid(v) || 'not a valid URL',
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end url with space'
        ],
        stringsRules: [
          (v) => !startsOrEndsWithSpace(v) || 'Do not start nor end string with space'
        ],
        groupsRules: [
          (v) => v.filter(x => startsOrEndsWithSpace(x)).length === 0 || 'Do not start nor end group name with space'
        ]
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
        const value = {
          documentLink: experiment && experiment.documentLink,
          description: experiment && experiment.description,
          groups: experiment && Array.from(experiment.groups)
        }
        this.$emit('input', this.buildResult(value))
        return value
      },

      validate () {
        return this.$refs.descEditingForm.validate()
      },

      getExpId () {
        return this.experiment && this.experiment.id
      },

      isUrlValid (value) {
        return isUri(value) !== undefined || !value
      },

      removeGroup (group) {
        const i = this.value.groups.indexOf(group)
        this.value.groups.splice(i, 1)
      },

      buildResult (value) {
        return new ExperimentDescEditingRecord({
          documentLink: value.documentLink,
          description: value.description,
          groups: List(value.groups)
        })
      },

      buildResultFromValue () {
        return this.buildResult(this.value)
      },

      updateDescriptions () {
        const newValue = this.buildResultFromValue()
        this.givenValue = newValue
        this.$emit('updateDescriptions', newValue)
      },

      closeDescriptions () {
        this.value = this.init(this.experiment)
        this.$emit('closeDescriptions')
      },

      descriptionsChanged () {
        return !this.givenValue.equals(this.buildResultFromValue())
      }
    }
  }
</script>
