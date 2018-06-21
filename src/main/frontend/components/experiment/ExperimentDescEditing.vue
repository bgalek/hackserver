<template>
  <v-form>
    <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
      <v-layout row align-center>

        <v-flex xs1>
          <v-tooltip right>
            <span>Describe shortly what you are going to test.</span>
            <v-icon
              slot="activator">help_outline</v-icon>
          </v-tooltip>
        </v-flex>
        <v-flex xs11>
          <v-text-field
            id="experimentDescriptionFormField"
            v-model="value.description"
            :rules="stringsRules"
            label="Description"
            v-on:input="inputEntered()">
          ></v-text-field>
        </v-flex>

      </v-layout>
      <v-layout row align-center>

        <v-flex offset-xs1>
          <v-text-field
            id="experimentDocumentationLinkFormField"
            v-model="value.documentLink"
            :rules="documentLinkRules"
            v-on:input="inputEntered()"
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
          <v-flex xs11>
            <v-select
              id="experimentAuthorizationGroupsFormField"
              label="Authorization groups"
              slot="activator"
              chips
              append-icon=""
              tags
              v-on:input="inputEntered()"
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
            </v-select>
          </v-flex>

      </v-layout>
    </v-container>

  </v-form>
</template>

<script>
  import { isUri } from 'valid-url'
  import { startsOrEndsWithSpace } from '../../utils/startsOrEndsWithSpace'

  export default {
    props: ['experiment'],

    data () {
      return {
        value: this.init(this.experiment),
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

    methods: {
      init (experiment) {
        const value = {
          documentLink: experiment && experiment.documentLink,
          groups: experiment && Array.from(experiment.groups),
          description: experiment && experiment.description
        }

        this.$emit('input', value)
        return value
      },

      inputEntered () {
        this.$emit('input', this.value)
      },

      isUrlValid (value) {
        return isUri(value) !== undefined || !value
      },

      removeGroup (group) {
        const i = this.value.groups.indexOf(group)
        this.groups = this.value.groups.splice(i, 1)
        this.inputEntered()
      }
    }
  }
</script>
