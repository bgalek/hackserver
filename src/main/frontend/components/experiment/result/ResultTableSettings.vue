<template>
  <v-card flat className="ma-0 pa-0">
    <v-card-text className="ma-0 pa-0">
      <v-btn-toggle v-model="device" row mandatory className="ma-0 pa-0">
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
    props: ['experiment'],

    data () {
      return {
        device: this.calcInitialDeviceClass()
      }
    },

    methods: {
      calcInitialDeviceClass () {
        const baseClass = this.experiment.getBaseDeviceClass()

        if (baseClass === 'desktop' || baseClass === 'tablet') {
          return baseClass
        }

        if (baseClass === 'phone') {
          return 'smartphone'
        }

        return 'all'
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
