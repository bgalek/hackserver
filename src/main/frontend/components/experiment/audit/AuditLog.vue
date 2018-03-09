<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg>
        <v-spacer></v-spacer>
        <v-card>
          <v-toolbar color="blue" dark>
            <v-toolbar-title>Audit log for {{ auditLog.experimentId }}</v-toolbar-title>
          </v-toolbar>

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
        </v-card>
        <v-alert v-if="errorOccurred()" color="error" icon="warning" value="true">
          Couldn't load audit log: {{ error.message }}
        </v-alert>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import { mapActions, mapState } from 'vuex'

  export default {
    created () {
      const experimentId = this.$route.params.experimentId
      this.getExperimentAuditLog({params: {experimentId: experimentId}})
    },

    computed: mapState({
      auditLog: state => state.experimentAuditLog.auditLog,
      error: state => state.experimentAuditLog.error
    }),
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
