<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg1>


  <br/>
  <br/>

  <v-alert
    :value="true"
    type="error"
    outline
  >
    Login error: <b>{{ this.authError }}</b>.

    <br/>Check if you have popups allowed on this page.

    <br/><br/>

    <v-btn href="/#/experiments" color="info">Retry</v-btn>

  </v-alert>


  <br/>
  <br/>

<div style="text-align: center;">
<pre style="display: inline-block; text-align: left;" >
 _             _          __       _ _          _
| | ___   __ _(_)_ __    / _| __ _(_) | ___  __| |
| |/ _ \ / _` | | '_ \  | |_ / _` | | |/ _ \/ _` |
| | (_) | (_| | | | | | |  _| (_| | | |  __/ (_| |
|_|\___/ \__, |_|_| |_| |_|  \__,_|_|_|\___|\__,_|
         |___/
</pre>
</div>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import { mapState } from 'vuex'

  export default {
    computed: mapState({
      authError: state => state.authentication.authError
    })
  }
</script>
