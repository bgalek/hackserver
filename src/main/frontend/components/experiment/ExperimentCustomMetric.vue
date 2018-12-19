<template>
  <div>
    <template v-if="isPresent()">
      <h3>Custom Metric Definition</h3>
      <h4>
        Metric name: {{getName()}}
      </h4>
      <h4>
        Success event:
      </h4>
      <custom-metric-definition-details :customMetric="getSuccessEvent()"/>
      <h4>
        View event:
      </h4>
      <custom-metric-definition-details :customMetric="getViewEvent()"/>
    </template>
  </div>
</template>
<script>
  import CustomMetricDefinitionDetails from './CustomMetricDefinitionDetails'

  export default {
    components: {CustomMetricDefinitionDetails},
    props: ['experiment'],
    computed: {
      customMetric: function () {
        return this.experiment.customMetricDefinition
      }
    },
    methods: {
      isPresent() {
        return this.customMetric && this.customMetric.name
      },
      getName() {
        return this.customMetric.name
      },
      getSuccessEvent() {
        return this.customMetric.successEventDefinition
      },
      getViewEvent() {
        return this.customMetric.viewEventDefinition
      }
    }
  }

</script>
