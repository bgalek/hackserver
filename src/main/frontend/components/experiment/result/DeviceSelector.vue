<template>
  <v-tabs v-model="value" right>
    <v-tab v-for="t in labels()" :key="t">
      {{ t }}
    </v-tab>
  </v-tabs>
</template>

<script>
  export default {
    props: ['selectedDevice','where'],

    data () {
      return {
        value: this.init()
      }
    },

    updated() {
      console.log('-- DeviceSelector.'+this.where+' : i am updated to ' , this.selectedDevice)
    },

    methods: {
      init () {
        console.log("this.selectedDevice", this.selectedDevice)
        return this.labels().findIndex(it => it === this.selectedDevice).toString()
      },

      labels() {
        return ['all', 'desktop', 'smartphone', 'tablet']
      },
    },

    watch: {
      value (value) {
        this.$emit('deviceChanged', {
          device: this.labels()[value]
        })
      }
    }
  }
</script>
