<template>
  <div>
    <v-select
      v-bind:items="predicates"
      v-model="selectedPredicateType"
      label="Predicate type"
      item-value="text"
    ></v-select>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        predicateTypes: ['internal', 'hash', 'cmuid_regexp'],
        selectedPredicateType: 'internal'
      }
    },

    methods: {
      selected() {
        this.$emit('predicateTypeSelected', {
          predicateType: this.selectedPredicateType
        })
      }
    }
  }
</script>
