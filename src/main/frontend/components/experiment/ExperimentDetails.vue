<template>
  <chi-panel title="Details">
    <v-layout>
    <v-flex xs6>
        <h3>Description</h3>
        {{ experiment.description || '-'}}

      <h3>Documentation link</h3>
      <a v-if=" experiment.documentLink !== '' " :href="experiment.documentLink">
        {{ experiment.documentLink }}
      </a>
      <div v-else>-</div>

      <h3>Author</h3>
      {{ experiment.author }}

      <h3>Authorized groups</h3>
       {{ experiment.groups.join(', ') || '-' }}

        <div v-if="experiment.activityPeriod">
          <h3>Activity period</h3>
          <v-layout row>
            <v-flex xs2>From: </v-flex><v-flex xs9>{{ experiment.fromDateString() }}</v-flex>
          </v-layout>
          <v-layout row>
            <v-flex xs2>To: </v-flex><v-flex xs9>{{ experiment.toDateString() }}</v-flex>
          </v-layout>
        </div>
        <h3>Status</h3>
        <v-layout row>
            <experiment-status :experiment="experiment" show-reporting-status="true"/>
        </v-layout>
    </v-flex>
    <v-flex xs6>
      <h3>Variants</h3>

      <v-list>
        <template v-for="v in experiment.variants">
          <v-list-tile>
            <v-list-tile-content>
              <v-badge>
                <v-chip :color="v.color" small :key="v.name" :disabled="true">
                  {{ v.name }}
                </v-chip>
                {{ v.predicatesInfo }}
              </v-badge>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </v-flex>
    </v-layout>
  </chi-panel>
</template>

<script>
  import ChiPanel from '../ChiPanel'
  import ExperimentStatus from './ExperimentStatus'

  export default {
    props: ['experiment'],

    components: {
      ExperimentStatus,
      ChiPanel
    },

    methods: {
    }
  }
</script>
