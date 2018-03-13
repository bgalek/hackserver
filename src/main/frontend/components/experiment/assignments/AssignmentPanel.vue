<template>

  <chi-panel title="Chi cookie assignments">
    <v-layout row>
      <v-flex>
        <assignment-button
          v-if="experiment"
          v-for="(variant, i) in experiment.variants"
          :key="variant.name"
          :color="variant.color"
          :title="variant.name"
          redirect-label="Assign me"
          :redirect-link="cookieBakerLink(experiment.id, variant.name)"
        >
        </assignment-button>

        <assignment-button
          v-if="experiment"
          key="turn_off"
          color="grey"
          title="Exclude me"
          redirect-label="Exclude me"
          :redirect-link="cookieBakerLink(experiment.id, '-')"
        >
        </assignment-button>

      </v-flex>
    </v-layout>

    <v-layout row>
      <v-flex>

      </v-flex>
    </v-layout>

    <div slot="footer">
      Use these buttons to open new browser tab with Chi cookie set to selected variant. Read the Docs about
      <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/chi_cookie/">Chi cookie</a>.
    </div>

  </chi-panel>

</template>

<script>
  import AssignmentButton from './AssignmentButton.vue'
  import ChiPanel from '../../ChiPanel.vue'
  import { cookieBakerLink } from '../../../utils/cookieBakerLink'

  export default {
    props: ['experiment'],

    components: {
      AssignmentButton,
      ChiPanel
    },

    data () {
      return {
        assignments_dialog: false
      }
    },

    methods: {
      cookieBakerLink (experimentId, variantName) {
        return cookieBakerLink(experimentId, variantName)
      }
    }
  }
</script>
