<template>
  <div>
      <v-tooltip bottom>
          <v-text-field
            v-model="description"
            label="Description"
            slot="activator"
            v-on:input="inputEntered()">
          ></v-text-field>
          <span>Describe shortly what you are going to test.</span>
      </v-tooltip>
  </div>
</template>

<script>
  export default {
    props: ['experiment'],

    computed: {
      description: function () {
        return this.experiment && this.experiment.description
      },
      documentLink: function () {
        return this.experiment && this.experiment.documentLink
      },
      groups: function () {
        return this.experiment && this.experiment.groups
      },

      value: function () {
        const initialValue = this.composeValue()
        this.$emit('input', initialValue)
        return initialValue
      }
    },

    methods: {
      inputEntered () {
        this.value = this.composeValue()
        console.log('emitted value ', this.value)
        this.$emit('input', this.value)
      },

      composeValue () {
        return {
          description: this.description,
          documentLink: this.experiment.documentLink,
          groups: this.experiment.groups
        }
      }
    }
  }
</script>
