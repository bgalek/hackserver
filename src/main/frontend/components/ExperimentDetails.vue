<template>
  <v-container fluid>
    <v-card flat>
      <v-card-text>
        <h3>Description</h3>
        {{ experiment.description }}
        <h3>Owner</h3>
        {{ experiment.owner }}
        <div v-if="experiment.activeFrom || experiment.activeTo">
          <h3>Dates</h3>
          Active from: {{ experiment.activeFrom}}
          Active to: {{ experiment.activeTo}}

        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>
<script>
  export default {
    props: ['experiment']
  }
</script>
