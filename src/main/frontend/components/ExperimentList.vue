<template>
  <v-list three-line>
    <experiment-list-element v-for="experiment in experiments" :key="experiment.id"
                             :experiment="experiment"
                             :linkToData="linkToData">
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
