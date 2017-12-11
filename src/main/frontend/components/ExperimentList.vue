<template>
  <v-list two-line>
    <experiment-list-element v-if="linkToData" v-for="experiment in sortedExperiments" :key="experiment.id"
                             :clickEvent="goToExperiment(experiment.id)"
                             :experiment="experiment"
                             :linkToData="linkToData">
    </experiment-list-element>
    <experiment-list-element v-if="!linkToData" v-for="experiment in sortedExperiments" :key="experiment.id"
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
    },

    computed: {
      sortedExperiments () {
        return this.sortExperiments(this.experiments)
      }
    },

    methods: {
      goToExperiment (experimentId) {
        return () => this.$router.push(`/experiments/${experimentId}`)
      },

      sortExperiments (experiments) {
        experiments.forEach(e => this.sortVariants(e.variants))

        const sortingKey = function (experiment) {
          return experiment.activeFrom ? experiment.fromDateString() : '0' + experiment.id
        }

        experiments.sort((l, r) => sortingKey(r).localeCompare(sortingKey(l)))

        return experiments
      },

      sortVariants (variants) {
        variants.sort((l, r) => {
          if (this.isBase(l)) {
            return 1
          }
          if (this.isBase(r)) {
            return -1
          }
          return l.name.localeCompare(r.name)
        })
      },

      isBase (variant) {
        return variant.name === 'base'
      }
    }
  }
</script>
