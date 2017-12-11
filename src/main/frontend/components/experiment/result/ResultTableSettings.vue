<template>
  <v-layout row wrap class="ma-0 pa-0">
    <v-flex xs6 sm5 md3 lg5 xl5 t0 class="ma-0 pa-0">
      <v-card flat class="ma-0 pa-0">
        <v-card-text class="ma-0 pa-0">
          <p class="ma-0 pa-0">
            Results until:
            <v-tooltip v-model="show" right>
              <v-btn icon slot="activator" class="ma-0 pa-0">
                <v-icon color="grey" class="ma-0 pa-0" small>help</v-icon>
              </v-btn>
              <span>Metrics and statistics are calculated for period: experiment start to picked date</span>
            </v-tooltip>
          </p>
          <v-menu class="ma-0 pa-0">
            <v-text-field
              class="ma-0 pa-0"
              slot="activator"
              v-model="toDate"
              prepend-icon="event"
            ></v-text-field>
            <v-date-picker class="ma-0 pa-0" v-model="toDate" :allowed-dates="allowedDates" no-title scrollable actions>
              <template slot-scope="{ save, cancel }">
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn flat color="primary" @click="cancel">Cancel</v-btn>
                  <v-btn flat color="primary" @click="save">OK</v-btn>
                </v-card-actions>
              </template>
            </v-date-picker>
          </v-menu>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-spacer></v-spacer>
    <v-flex xs6 sm7 md7 lg7 xl7 class="ma-0 pa-0">
      <v-card flat class="ma-0 pa-0">
        <v-card-text class="ma-0 pa-0">
          <p class="mt-0 pt-0">Device:</p>
          <v-btn-toggle v-model="device" row class="ma-0 pa-0">
            <v-btn flat value="all">
              All
            </v-btn>
            <v-btn flat value="desktop">
              Desktop
            </v-btn>
            <v-btn flat value="smartphone">
              Smartphone
            </v-btn>
            <v-btn flat value="tablet">
              Tablet
            </v-btn>
          </v-btn-toggle>
        </v-card-text>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
  import Moment from 'moment'
  import { extendMoment } from 'moment-range'

  const moment = extendMoment(Moment)

  export default {
    props: ['initialToDate', 'initialDevice'],

    data () {
      return {
        allowedDates: moment.range(new Date('2017-01-01'), new Date()),
        toDate: this.initialToDate,
        device: this.initialDevice
      }
    },

    watch: {
      device (device) {
        this.$emit('settingsChanged', {
          device: device,
          toDate: this.toDate
        })
      },

      toDate (date) {
        this.$emit('settingsChanged', {
          device: this.device,
          toDate: date
        })
      }
    }
  }
</script>
