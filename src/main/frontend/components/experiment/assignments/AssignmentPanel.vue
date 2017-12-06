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
  </div>


</template>

<script>
  import {mapState, mapActions} from 'vuex'
  import {variantColor} from '../../../utils/variantColor'
  import AssignmentButton from './AssignmentButton.vue'

  export default {
    props: ['experiment'],

    components: {
      AssignmentButton
    },

    methods: {
      variantColor (i) {
        return variantColor(i)
      }
    }
  }
</script>
