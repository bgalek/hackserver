<template>
  <chi-panel title="Details">
    <v-layout>
    <v-flex xs5>
        <h3>Description</h3>
        {{ experiment.description || '-'}}

      <h3>Documentation link</h3>
      <a v-if=" experiment.documentLink !== '' " :href="experiment.documentLink">
        {{ experiment.documentLink }}
      </a>
      <div v-else>-</div>

      <h3 class="mt-2">Author</h3>
      {{ experiment.author }}

      <h3>Authorized groups</h3>
       {{ experiment.groups.join(', ') || '-' }}

      <div v-if="experiment.activityPeriod">
        <h3 class="mt-2">Activity period</h3>
        <v-layout row>
          <v-flex xs2>From: </v-flex><v-flex xs9>{{ experiment.fromDateString() }}</v-flex>
        </v-layout>
        <v-layout row>
          <v-flex xs2>To: </v-flex><v-flex xs9>{{ experiment.toDateString() }}</v-flex>
        </v-layout>
      </div>

        <h3 class="mt-2">Status</h3>
        <v-layout row>
            <experiment-status :experiment="experiment" :show-reporting-status="true"/>
        </v-layout>

    </v-flex>

    <v-flex xs7>
      <h3>Variants</h3>

      <v-list>
        <template v-for="v in experiment.variants">
          <v-list-tile>
            <v-list-tile-content>
              <v-badge>
                <v-chip :color="v.color+' lighten-2'"
                        small :key="v.name" :disabled="true">
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

    <v-layout row class="mt-2">
      <v-flex xs5>
        <h3>Reporting type</h3>
        <v-chip outline disabled :color="'black'">
          {{ experiment.reportingType }}
        </v-chip>
      </v-flex>

      <v-flex xs7 v-if="experiment.eventDefinitionsAvailable()">
        <h3>NGA event definitions</h3>
        <experiment-event-filters
          :readOnly="true"
          :initData="eventDefinitions">
        </experiment-event-filters>

      </v-flex>
    </v-layout>

  </chi-panel>
</template>

<script>
  import ChiPanel from '../ChiPanel'
  import ExperimentStatus from './ExperimentStatus'
  import ExperimentEventFilters from './ExperimentEventFilters'

  export default {
    props: ['experiment', 'eventDefinitions'],

    components: {
      ExperimentStatus,
      ChiPanel,
      ExperimentEventFilters
    }
  }
</script>
