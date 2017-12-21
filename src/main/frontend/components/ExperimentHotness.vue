<template>
  <v-tooltip v-if="value > 0" right>
    <v-icon :color="color" slot="activator">whatshot</v-icon>
    <span>{{ label }}: {{ value }}</span>
  </v-tooltip>
</template>

<script>
  export default {
    props: ['label', 'value'],

    computed: {
      color () {
        return [
          [1000000, 'red'], [400000, 'orange'], [100000, 'yellow'], [0, 'yellow lighten-4']
        ].find(([level, color]) => {
          return this.value >= level
        })[1]
      }
    }
  }
</script>
