<template>
  <v-tooltip top>
    <v-btn
      :color="color"
      @click="goToCookieBaker(experimentId, variantName)"
      class="white--text"
      slot="activator"
    >
      {{ variantName }}
      <v-icon right>ondemand_video</v-icon>
    </v-btn>
    <span>open new browser tab with session assigned to {{ variantName }}</span>
  </v-tooltip>
</template>

<script>
  import {cookieBakerHost} from '../../../utils/cookieBakerHost'

  export default {
    props: ['experimentId', 'variantName', 'color'],

    methods: {
      goToCookieBaker (experimentId, variantName) {
        let protocol = 'https://'
        let host = cookieBakerHost()
        let url = protocol + host + `/chi/cookie-baker.html?chi=${experimentId}!${variantName}&redirect=${protocol + host + '/'}`
        window.open(url, '_blank')
      }
    }
  }
</script>
