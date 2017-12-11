<template>
  <v-layout row wrap>
    <v-flex xs11 sm5>
      <v-card flat>
        <v-card-text>
          <v-menu>
            <v-text-field
              slot="activator"
              label="Results to day:"
              v-model="toDate"
              prepend-icon="event"
            ></v-text-field>
            <v-date-picker v-model="toDate" :allowed-dates="allowedDates" no-title scrollable actions>
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
    <v-flex xs11 sm5>
      <v-card flat>
        <v-card-text>
          <p>Device:</p>
          <v-btn-toggle v-model="device" row>
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
