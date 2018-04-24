<template>
  <v-card flat class="ma-0 pa-0">
    <v-card-text class="ma-0 pa-0">
      <v-btn-toggle v-model="variantName" row mandatory class="ma-0 pa-0">
        <v-btn v-for="v in experiment.variants" flat :value="v.name" :key="v.name">
          {{ v.name }}
        </v-btn>
      </v-btn-toggle>
    </v-card-text>
  </v-card>
</template>

<script>
  export default {
    props: ['experiment', 'selectedVariantName'],

    data () {
      return {
        variantName: this.getInitialVariantName()
      }
    },

    methods: {
      getInitialVariantName () {
        return this.selectedVariantName || this.experiment.variants[0].name
      }
    },

    watch: {
      variantName (variantName) {
        this.$emit('variantNameChanged', { variantName })
      }
    }
  }
</script>
