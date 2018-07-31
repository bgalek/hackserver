<template>
  <v-tabs v-model="value" right>
    <v-tab v-for="t in labels()" :key="t">
      {{ t }}
    </v-tab>
  </v-tabs>
</template>

<script>
  export default {
    props: ['selectedDevice'],

    data () {
      return {
        value: this.init()
      }
    },

    methods: {
      init () {
        return this.labels().findIndex(it => it === this.selectedDevice).toString()
      },

      labels () {
        return ['all', 'desktop', 'smartphone']
      }
    },

    watch: {
      value (value) {
        this.$emit('deviceChanged', {
          device: this.labels()[value]
        })
      },

      selectedDevice (value) {
        this.value = this.init()
      }
    }
  }
</script>
