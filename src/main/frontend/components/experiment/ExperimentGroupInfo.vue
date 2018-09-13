<template>
  <div>

  <template v-if="isPresent()">

    <v-chip outline color="black">
      {{ this.experimentGroup.id }}
    </v-chip>

    = {

    <template v-for="experimentId, i in experimentGroup.experimentIds">

      <template v-if="isSelf(experimentId)">
        {{ experimentId }}
      </template>

      <template v-if="!isSelf(experimentId)">
        <a :href="'/#/experiments/' + experimentId">
          {{ experimentId }}
        </a>
      </template>

      <template v-if="i < experimentGroup.size() - 1">, </template>

    </template>

    }
  </template>

  <template v-if="!isPresent()">
    -
  </template>

  </div>
</template>

<script>
  export default {
    props: ['experiment'],

    data () {
      return {
        experimentGroup: this.experiment.experimentGroup
      }
    },

    methods: {
      isSelf (experimentId) {
        return this.experiment.id === experimentId
      },

      isPresent () {
        return this.experimentGroup && this.experimentGroup.id
      }
    }
  }
</script>
