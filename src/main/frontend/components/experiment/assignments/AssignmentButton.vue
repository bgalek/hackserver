<template>
  <v-menu open-on-hover bottom offset-y>
    <v-btn flat :color="color" slot="activator" style="text-transform: none">{{ title }} <v-icon right>ondemand_video</v-icon> </v-btn>
    <v-list>
      <v-list-tile @click="goToCookieBaker(redirectLink)">
        <v-list-tile-title>Assign me and redirect</v-list-tile-title>
      </v-list-tile>
      <v-list-tile  @click="copyLinkToClipboard(redirectLink)">
        <v-list-tile-title>Copy assignment link</v-list-tile-title>
      </v-list-tile>
    </v-list>
  </v-menu>
</template>

<script>
  export default {
    props: ['title', 'color', 'redirectLink'],
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
