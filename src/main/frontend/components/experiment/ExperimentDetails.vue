<template>
  <chi-panel title="Details">
    <v-layout>
    <v-flex xs6>
        <h3>Description</h3>
        {{ experiment.description }}
        <h3>Owner</h3>
        {{ experiment.owner }}
        <div v-if="experiment.activeFrom || experiment.activeTo">
          <h3>Dates</h3>
          <v-layout row>
            <v-flex xs2>Active from: </v-flex><v-flex xs9>{{ experiment.fromDateString() }}</v-flex>
          </v-layout>
          <v-layout row>
            <v-flex xs2>Active to: </v-flex><v-flex xs9>{{ experiment.toDateString() }}</v-flex>
          </v-layout>
        </div>
        <h3>Status</h3>
        <v-layout row>

            <v-chip outline disabled :color="activityButtonClass()">
              {{ experiment.status() }}
            </v-chip>

            <v-chip disabled outline :color="reportingEnabledButtonClass()" >
              {{ reportingEnabledButtonText() }}
            </v-chip>

        </v-layout>
    </v-flex>
    <v-flex xs6>
      <h3>Variants</h3>

      <v-list two-line>
        <template v-for="v in experiment.variants">
          <v-list-tile>
            <v-list-tile-content>
              <v-badge>
                <v-chip :color="v.color" small :key="v.name" :disabled="true">
                  {{ v.name }}
                </v-chip>
              </v-badge>
              <v-list-tile-sub-title v-html="v.predicatesInfo"></v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </v-flex>
    </v-layout>

  </chi-panel>
</template>

<script>
  import ChiPanel from '../ChiPanel.vue'

  export default {
    props: ['experiment'],

    components: {
      ChiPanel
    },

    methods: {
      activityButtonClass () {
        let colors = {
          PLANNED: 'blue',
          ACTIVE: 'green',
          ENDED: 'black'
        }
        return colors[this.experiment.status()]
      },

      reportingEnabledButtonClass () {
        return this.experiment.reportingEnabled ? 'green' : 'red'
      },

      reportingEnabledButtonText () {
        return this.experiment.reportingEnabled ? 'Reporting enabled' : 'Reporting disabled'
      }
    }
  }
</script>
