<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg>
        <chi-panel :title="panelTitle">
            <v-container grid-list-md>
              <v-layout row wrap>
                <template v-for="item in auditLog.changes">
                  <v-flex xs12>
                    <span>{{ item.formattedDate() }}</span>
                    <span class="grey--text"> by {{ item.author }}</span>
                  </v-flex>
                  <v-flex xs12>
                    <code class="changelog">{{ item.changelog }}</code>
                  </v-flex>
                </template>
              </v-layout>
            </v-container>
        </chi-panel>
        <v-alert v-if="errorOccurred()" color="error" icon="warning" value="true">
          Couldn't load audit log: {{ error.message }}
        </v-alert>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import { mapActions, mapState } from 'vuex'
  import ChiPanel from '../../ChiPanel.vue'

  export default {
    components: {
      ChiPanel
    },
    created () {
      const experimentId = this.$route.params.experimentId
      this.getExperimentAuditLog({params: {experimentId: experimentId}})
    },

    computed: {
      ...mapState({
        auditLog: state => state.experimentAuditLog.auditLog,
        error: state => state.experimentAuditLog.error
      }),

      panelTitle () {
        return `Audit log for ${this.auditLog.experimentId}`
      }
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
