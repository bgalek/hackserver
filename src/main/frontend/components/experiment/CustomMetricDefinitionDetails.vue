<template>
  <v-layout row class="mt-2">

    <template v-for="(item, key) in customMetric">

      <template>
        <v-chip outline color="black" small disabled>
          {{key}}: {{ item }}
        </v-chip>
      </template>

    </template>
  </v-layout>
</template>
<script>
  export default {
    props: ['customMetric']
  }
</script>
