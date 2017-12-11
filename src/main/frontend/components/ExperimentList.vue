<template>
  <div id="experiments" v-if="experiments.length">
    <v-list two-line>
      <v-list-tile v-for="experiment in experiments" :key="experiment.id" @click="goToExperiment(experiment.id)">
        <v-list-tile-content>
          <v-list-tile-title v-html="experiment.id"></v-list-tile-title>
          <v-list-tile-sub-title v-html="experiment.desc"></v-list-tile-sub-title>
          <v-list-tile-sub-title v-html="experiment.activeFrom"></v-list-tile-sub-title>
        </v-list-tile-content>
        <v-list-tile-action>
          <v-badge>
            <v-chip small :color="variantColor(i, variant)" v-for="(variant, i) in experiment.variants" :key="variant.name" :disabled="true">
              {{ variant.name }}
            </v-chip>

            <v-tooltip left>
              <v-btn flat icon color="indigo" slot="activator" @click="goToPivot(experiment.id)">
                <v-icon>show_chart</v-icon>
              </v-btn>
              <span>Pivot Stats</span>
            </v-tooltip>
          </v-badge>
        </v-list-tile-action>
      </v-list-tile>
    </v-list>
  </div>
</template>

<script>

</script>
