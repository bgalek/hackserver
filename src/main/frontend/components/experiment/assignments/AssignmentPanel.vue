<template>

  <chi-panel title="Chi cookie assignments">

      <assignment-button
        v-if="experiment"
        v-for="(variant, i) in experiment.variants"
        :key="variant.name"
        :color="variantColor(12)"
        :variant-name="variant.name"
        :experiment-id="experiment.id"
      >
      </assignment-button>

      <div slot="footer">
        Use these buttons to set Chi cookie. Read the Docs about
        <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/chi_cookie/">Chi cookie</a>.
      </div>

  </chi-panel>

</template>

<script>
  import {variantColor} from '../../../utils/variantColor'
  import AssignmentButton from './AssignmentButton.vue'
  import ChiPanel from '../../ChiPanel.vue'

  export default {
    props: ['experiment'],

    components: {
      AssignmentButton,
      ChiPanel
    },

    methods: {
      variantColor (i) {
        return variantColor(i)
      }
    }
  }
</script>
