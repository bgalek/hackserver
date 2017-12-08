<template>
  <v-container fluid>
    <v-card flat>
      <v-card-text>
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
            <v-flex xs2>Active to: </v-flex><v-flex xs9ą>{{ experiment.toDateString() }}</v-flex>
          </v-layout>
        </div>
        <h3>Status</h3>
        <v-layout row>
            <v-btn
              :color="activityButtonClass()"
              class="white--text"
            >
              {{ experiment.status() }}
            </v-btn>
            <v-btn
              :color="reportingEnabledButtonClass()"
              class="white--text"
            >
              {{ reportingEnabledButtonText() }}

            </v-btn>
        </v-layout>
      </v-card-text>
    </v-card>
  </v-container>
</template>
<script>
  export default {
    props: ['experiment'],

    methods: {
      activityButtonClass () {
        let colors = {
          PLANNED: "blue",
          ACTIVE: "green",
          ENDED: 'black'
        }
        console.log('status',this.experiment.status() )
        return colors[this.experiment.status()]
      },

      reportingEnabledButtonClass () {
        return this.experiment.reportingEnabled ? 'green' : 'red';
      },

      reportingEnabledButtonText () {
        return this.experiment.reportingEnabled ? 'Reporting enabled' : 'Reporting disabled';
      }
    },
  }
</script>
