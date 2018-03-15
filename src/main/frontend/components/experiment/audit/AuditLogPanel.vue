<template>
  <chi-panel title="Audit log">
      <v-container grid-list-md>
        <v-layout v-if="!errorOccurred()" row wrap>
          <template v-for="item in auditLog.changes">
            <v-flex xs12>
              <span>{{ item.formattedDate() }}</span>
              <span class="grey--text"> by {{ item.author }}</span>
            </v-flex>
            <v-flex xs12>
              <code class="changelog">{{ item.changelog }}</code>
            </v-flex>
          </template>
          <span v-if="!auditLog.hasChanges()">
            No data available.
          </span>
        </v-layout>
        <v-alert v-if="errorOccurred()" color="error" icon="warning" value="true">
          Couldn't load audit log: {{ error.message }}
        </v-alert>
      </v-container>
  </chi-panel>
</template>

<script>
  import { mapActions, mapState } from 'vuex'
  import ChiPanel from '../../ChiPanel.vue'

  export default {
    props: {
      experimentId: String
    },

    components: {
      ChiPanel
    },

    mounted () {
      this.getExperimentAuditLog({params: {experimentId: this.experimentId}})
    },

    computed: {
      ...mapState({
        auditLog: state => state.experimentAuditLog.auditLog,
        error: state => state.experimentAuditLog.error
      })
    },

    methods: {
      ...mapActions(['getExperimentAuditLog']),

      errorOccurred () {
        return this.error instanceof Error
      }
    }
  }
</script>

<style scoped>

  .changelog {
    display: block;
  }

</style>
