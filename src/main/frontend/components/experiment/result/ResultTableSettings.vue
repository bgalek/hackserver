<template>
  <v-container fluid>
    <v-layout row wrap>
      <v-flex xs4 sm3 md3 lg3 xl3>
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

      <v-flex xs8 sm9 md9 lg9 xl9>
        <v-card flat>
          <v-card-text>
            <v-radio-group v-model="device" row>
              <v-radio flat id="all" label="All" value="all"></v-radio>
              <v-radio flat id="desktop" label="Desktop" value="desktop"></v-radio>
              <v-radio flat id="smartphone" label="Smartphone" value="smartphone"></v-radio>
              <v-radio flat id="tablet" label="Tablet" value="tablet"></v-radio>
            </v-radio-group>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
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
