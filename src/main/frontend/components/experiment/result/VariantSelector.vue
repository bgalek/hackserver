<template>
  <v-card flat class="ma-0 pa-0">
    <v-card-text class="ma-0 pa-0">
      <v-btn-toggle light v-model="variantName" row mandatory class="ma-0 pa-0">
        <v-btn depressed v-for="name in getVariantNames()" flat :value="name" :key="name">
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
        variantName: this.selectedVariantName || this.getVariantNames().find()
      }
    },

    methods: {
      getVariantNames () {
        return Array.from(new Set(this.experiment.variants.map(v => v.name).filter(n => this.showBase || n !== 'base')))
      }
    },

    watch: {
      variantName (variantName) {
        this.$emit('variantNameChanged', { variantName })
      },

      selectedVariantName (value) {
        this.variantName = value
      }
    }
  }
</script>
