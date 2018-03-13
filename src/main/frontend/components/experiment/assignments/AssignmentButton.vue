<template>
  <v-menu open-on-hover bottom offset-y>
    <v-btn flat :color="color+' darken-2'" slot="activator" style="text-transform: none">{{ title }}
      <v-icon right>ondemand_video</v-icon>
    </v-btn>
    <v-list>
      <v-list-tile @click="goToCookieBaker(redirectLink)">
        <v-list-tile-title>{{ redirectLabel }} and redirect</v-list-tile-title>
      </v-list-tile>
      <v-list-tile  @click="copyLinkToClipboard(redirectLink)">
        <v-list-tile-title>Copy assignment link</v-list-tile-title>
      </v-list-tile>
    </v-list>
  </v-menu>
</template>

<script>
  export default {
    props: ['title', 'color', 'redirectLink', 'redirectLabel'],
    methods: {
      goToCookieBaker (redirectLink) {
        window.open(redirectLink, '_blank')
      },
      copyLinkToClipboard (redirectLink) {
        this.$clipboard(redirectLink)
      }
    }
  }
</script>
