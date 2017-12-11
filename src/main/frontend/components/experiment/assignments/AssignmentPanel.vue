<template>
  <div>

    <v-toolbar color="blue" style="margin-top: 20px;">
      <v-toolbar-title class="white--text">Chi cookie assignments</v-toolbar-title>
    </v-toolbar>
    <v-card tile="true" style="padding:10px;">

    <assignment-button
      v-if="experiment"
      v-for="(variant, i) in experiment.variants"
      :key="variant.name"
      :color="variantColor(12)"
      :variant-name="variant.name"
      :experiment-id="experiment.id"
    >
    </assignment-button>

    <div>
      Use these buttons to set Chi cookie. Read the Docs about
      <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/chi_cookie/">Chi cookie</a>.
    </div>
    </v-card>
  </div>


</template>

<script>
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
