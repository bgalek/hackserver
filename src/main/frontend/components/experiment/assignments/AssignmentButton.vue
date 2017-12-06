<template>
  <v-btn
    :color="color"
    @click="goToCookieBaker(experimentId, variantName)"
    class="white--text"
  >
    {{ variantName }}
    <v-icon right>edit</v-icon>
  </v-btn>
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
