<template>
  <v-card flat class="ma-0 pa-0">
    <v-card-text class="ma-0 pa-0">
      <v-btn-toggle v-model="variantName" row mandatory class="ma-0 pa-0">
        <v-btn v-for="name in getVariantNames()" flat :value="name" :key="name">
          {{ name }}
        </v-btn>
      </v-btn-toggle>
    </v-card-text>
  </v-card>
</template>

<script>
  export default {
    props: ['experiment', 'selectedVariantName', 'showBase'],

    data () {
      return {
        variantName: this.getInitialVariantName()
      }
    },

    methods: {
      getInitialVariantName () {
        return this.selectedVariantName || this.getVariantNames().find()
      },

      getVariantNames () {
        return this.experiment.variants.map(v => v.name).filter(n => this.showBase || n !== 'base')
      }
    },

    watch: {
      variantName (variantName) {
        this.$emit('variantNameChanged', { variantName })
      }
    }
  }
</script>
