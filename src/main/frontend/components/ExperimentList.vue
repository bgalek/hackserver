<template>
  <v-list three-line>
    <experiment-list-element v-for="(experiment, index) in experiments" :key="experiment.id"
                             :experiment="experiment"
                             :linkToData="linkToData"
                             :index="index">
    </experiment-list-element>
  </v-list>
</template>

<script>
  import ExperimentListElement from './ExperimentListElement.vue'

  export default {
    props: ['experiments', 'linkToData'],

    components: {
      ExperimentListElement
    }
  }
</script>
