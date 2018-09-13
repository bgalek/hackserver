<template>
  <chi-panel title="Details">
    <v-layout>
    <v-flex xs5>
        <h3>Description</h3>
        {{ experiment.description || '-'}}

      <h3 class="mt-2">Documentation link</h3>
      <a v-if=" experiment.documentLink !== '' " :href="experiment.documentLink">
        {{ documentLinkText() }}
      </a>
      <div v-else>-</div>

      <h3 class="mt-2"> Experiment group
        <v-tooltip right  close-delay="1000">
          <span>
            You can create a group of mutually exclusive experiments.<br/>
            Experiments in a group will not intersect with each other.<br/>
            Useful when you have many experiments in the same area of the system.<br/>
            <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/#grupa-eksperymentow">
            Read more about groups in χ Docs</a>
          </span>
          <v-icon slot="activator">help_outline</v-icon>
        </v-tooltip>
      </h3>
      <experiment-group-info :experiment="experiment"/>

      <h3 class="mt-2">Author</h3>
      {{ experiment.author }}

      <h3 class="mt-2">Authorized groups</h3>
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

      <div v-if="experiment.activityPeriod">
        <h3 class="mt-2">Significance level
          <v-tooltip right>
          <span>
            Since you are running multiple tests
            (number of devices * number of metrics * number of variants = {{ experiment.bonferroniCorrection }}),
            <br/>
            the desired overall significance level 𝜶 = {{ experiment.desiredAlpha() }}
            is adjusted. <br/>
            Each individual hypothesis is tested with
            𝜶 = {{ experiment.desiredAlpha() }}/{{ experiment.bonferroniCorrection }} = {{ experiment.usedAlpha() }}.
          </span>
            <v-icon slot="activator">help_outline</v-icon>
          </v-tooltip>
        </h3>
        <v-layout row>
          <v-flex xs5>Desired 𝜶: </v-flex><v-flex xs5>{{ experiment.desiredAlpha() }}</v-flex>
        </v-layout>
        <v-layout row>
          <v-flex xs5>Bonferroni correction: </v-flex><v-flex xs5>{{ experiment.bonferroniCorrection }}</v-flex>
        </v-layout>
        <v-layout row>
          <v-flex xs5>Used 𝜶: </v-flex><v-flex xs5>{{ experiment.usedAlpha() }}</v-flex>
        </v-layout>
      </div>

    </v-flex>
    </v-layout>

    <v-layout row class="mt-2">
      <v-flex xs5>
        <h3>Reporting type
          <v-tooltip right  close-delay="1000">
          <span>
              <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/reporting/">
               Read about reporting types in χ Docs</a>
          </span>
            <v-icon slot="activator">help_outline</v-icon>
          </v-tooltip>
        </h3>
        <v-chip outline disabled :color="'black'">
          {{ experiment.reportingType }}
        </v-chip>

        <v-chip outline :color="reportingEnabledButtonClass()" >
          {{ reportingEnabledButtonText() }}
        </v-chip>

      </v-flex>

      <v-flex xs7 v-if="experiment.eventDefinitionsAvailable()">
        <h3>NGA event definitions</h3>
        <experiment-event-filters-editing
          :experiment="experiment"
          :readOnly="true">
        </experiment-event-filters-editing>

      </v-flex>
    </v-layout>

  </chi-panel>
</template>

<script>
  import ChiPanel from '../ChiPanel'
  import ExperimentStatus from './ExperimentStatus'
  import ExperimentGroupInfo from './ExperimentGroupInfo'
  import ExperimentEventFiltersEditing from './ExperimentEventFiltersEditing'
  import _ from 'lodash'

  export default {
    props: ['experiment'],

    components: {
      ExperimentEventFiltersEditing,
      ExperimentGroupInfo,
      ExperimentStatus,
      ChiPanel
    },

    methods: {
      documentLinkText () {
        return _.truncate(this.experiment.documentLink, {
          'length': 40
        })
      },

      reportingEnabledButtonClass () {
        return this.experiment.reportingEnabled ? 'green' : 'red'
      },

      reportingEnabledButtonText () {
        return this.experiment.reportingEnabled ? 'enabled' : 'disabled'
      }
    }
  }
</script>
