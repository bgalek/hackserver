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
            v-model="value.description"
            label="Description"
            v-on:input="inputEntered()">
          ></v-text-field>
        </v-flex>

      </v-layout>
      <v-layout row align-center>

        <v-flex offset-xs1>
          <v-text-field
            v-model="value.documentLink"
            :rules="documentLinkRules"
            v-on:input="inputEntered()"
            label="Documentation link"
          ></v-text-field>
        </v-flex>

      </v-layout>
      <v-layout row align-center>

          <v-flex xs1>
            <v-tooltip right>
              <span>
                Let your team manage this experiment.
                Provide a group name, for example: <code>Tech Content Team</code>.
                <br/>
                Hit <b>ENTER</b> to add a group.
              </span>
              You can add more than one group.
              <br/>
              Check your groups on your JIRA profile &mdash; https://jira.allegrogroup.com/secure/ViewProfile.jspa
              <v-icon
                slot="activator">help_outline</v-icon>
            </v-tooltip>
          </v-flex>
          <v-flex xs11>
            <v-select
              label="Authorization groups"
              slot="activator"
              chips
              append-icon=""
              tags
              v-on:input="inputEntered()"
              v-model="value.groups">
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
  import {isUri} from 'valid-url'

  export default {
    props: ['experiment'],

    data () {
      console.log('this', this)
      return {
        value: this.init(this.experiment),
        documentLinkRules: [
          (v) => this.isUrlValid(v) || 'not a valid URL'
        ]
      }
    },

    methods: {
      init (experiment) {
        console.log('experiment', experiment)
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
