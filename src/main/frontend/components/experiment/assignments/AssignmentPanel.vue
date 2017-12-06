<template>
  <div>
    <h2>Assignments</h2>
    <assignment-button
      v-if="experiment"
      v-for="(variant, i) in experiment.variants"
      :key="variant.name"
      :color="variantColor(i)"
      :variant-name="variant.name"
      :experiment-id="experiment.id"
    >
    </assignment-button>

    <v-alert v-if="error" color="error" icon="warning" value="true">
      Couldn't load experiment {{ $route.params.experimentId }} : {{ error.message }}
    </v-alert>

    <p class="text-xs-center">
      <v-progress-circular v-if="pending" indeterminate :size="70" :width="7" color="purple"></v-progress-circular>
    </p>
  </div>


</template>

<script>
  import {mapState, mapActions} from 'vuex'
  import {variantColor} from '../../../utils/variantColor'
  import AssignmentButton from './AssignmentButton.vue'

  export default {
    props: ['experimentId'],

    components: {
      AssignmentButton
    },

    computed: mapState({
      experiment: state => state.experiment.experiment,
      error: state => state.experiment.error.experiment,
      pending: state => state.experiment.pending.experiment
    }),

    mounted () {
      this.getExperiment({params: {experimentId: this.experimentId}})
    },

    methods: {
      ...mapActions(['getExperiment']),

      variantColor (i) {
        return variantColor(i)
      }
    }
  }
</script>
