<template>
  <v-container>
    <v-layout>
      <v-flex offset-md1 md10 lg9 offset-xl2 xl8>
        <h1>Experiment: {{ $route.params.experimentId }}</h1>

        <h2>Variants</h2>
        <div id="variants" v-if="experiment.variants">
          <v-list two-line>
            <v-list-tile v-for="(variant, i) in experiment.variants" :key="variant.name" @click="">
              <v-list-tile-content>
                <v-list-tile-title v-html="variant.name"></v-list-tile-title>
              </v-list-tile-content>
              <v-list-tile-action>
                <v-btn @click="goToCookieBaker(experiment.id, variant.name)">
                  Assign
                </v-btn>
              </v-list-tile-action>
            </v-list-tile>
          </v-list>
        </div>

        <v-alert v-if="error" color="error" icon="warning" value="true">
          Couldn't load experiment {{ $route.params.experimentId }} : {{ error.message }}
        </v-alert>

        <p class="text-xs-center">
          <v-progress-circular v-if="pending" indeterminate :size="70" :width="7" color="purple"></v-progress-circular>
        </p>
    </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { variantColor } from '../utils/variantColor'
import { cookieBakerHost } from '../utils/cookieBakerHost'

export default {
  mounted () {
    this.getExperiment({ params: { experimentId: this.$route.params.experimentId } })
  },

  computed: mapState({
    experiment: state => state.experiment.experiment,
    error: state => state.experiment.error.experiment,
    pending: state => state.experiment.pending.experiment
  }),

  methods: {
    ...mapActions(['getExperiment']),

    variantColor (i) {
      return variantColor(i)
    },

    goToCookieBaker (experimentId, variantName) {
      let protocol = 'https://'
      let host = cookieBakerHost()
      let url = protocol + host + `/chi/cookie-baker.html?chi=${experimentId}!${variantName}&redirect=${protocol + host + '/'}`
      window.open(url, '_blank')
    }
  }
}
</script>
