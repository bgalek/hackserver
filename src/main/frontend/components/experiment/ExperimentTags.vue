<template>
  <div>

    <template v-if="experimentTags">

      <template v-for="tag in experimentTags">

        <template>
          <v-chip outline color="black" small disabled>
            {{ tag }}
          </v-chip>
        </template>

      </template>

    </template>

    <template v-if="experimentTags.length < 1">
      -
    </template>

  </div>
</template>

<script>
  export default {
    props: ['experiment'],

    computed: {
      experimentTags: function () {
        return this.experiment.tags
      }
    }
  }
</script>
