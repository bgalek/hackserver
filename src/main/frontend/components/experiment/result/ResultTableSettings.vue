<template>
  <v-card flat class="ma-0 pa-0">
    <v-card-text class="ma-0 pa-0">
      <v-btn-toggle v-model="device" row mandatory class="ma-0 pa-0">
        <v-btn flat value="all">
          All
        </v-btn>
        <v-btn flat value="desktop">
          Desktop
        </v-btn>
        <v-btn flat value="smartphone">
          Smartphone
        </v-btn>
        <v-btn flat value="tablet">
          Tablet
        </v-btn>
      </v-btn-toggle>
    </v-card-text>
  </v-card>
</template>

<script>
  export default {
    props: ['initialDevice'],

    data () {
      return {
        device: this.initialDevice
      }
    },

    watch: {
      device (device) {
        this.$emit('settingsChanged', {
          device: device
        })
      }
    }
  }
</script>
