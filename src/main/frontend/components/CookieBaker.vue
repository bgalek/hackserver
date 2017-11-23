<template>
  <v-container><v-layout><v-flex offset-md1 md10 lg9 offset-xl2 xl8>
    <h1>Cookie Baker</h1>

    <p>With &chi; cookie you can force a specific experiment variant
       or disable experiments.</p>

    <h2>Usage</h2>

    <v-list>
      <v-list-tile>
        <code>-</code> : Disable all experiments
      </v-list-tile>
      <v-list-tile>
        <code>e1!v1</code> : Force variant v1 for experiment e1
      </v-list-tile>
      <v-list-tile>
        <code>e1!-</code> : Disable experiment e1
      </v-list-tile>
      <v-list-tile>
        <code>e1!v1,e2!v2</code> : Force variant v1 for experiment e1 and variant v2 for experiment e2
      </v-list-tile>
    </v-list>

    <v-form v-model="valid">
      <v-text-field label="Overrides" v-model="chi" required
        :rules="[overrideValidator]"
        :counter="maxOverrideLength"></v-text-field>

      <v-select label="Choose expire time" v-model="expire" :items="expires" item-text="description" required ></v-select>

      <v-text-field v-model="redirectPath" placeholder="experiment-path" label="Redirect path"
        :prefix="'https://' + cookieBakerHost + '/'"></v-text-field>
    </v-form>

    <div v-if="valid">
      <h4>URL for sharing</h4>
      <v-alert outline color="info" class="text-xs-center" :value="true" @click="copyToClipboard">
        <h3>{{ sharingUrl }}</h3>
      </v-alert>

      <v-btn block color="primary" @click="goAndSet">Go & Set chi cookie!</v-btn>

      <v-snackbar :timeout="3000" :top="true" v-model="copied" color="success">
        URL for sharing has been copied to the clipboard
      </v-snackbar>
    </div>
  </v-flex></v-layout></v-container>
</template>

<script>
export default {
  data () {
    return {
      chi: '',
      expire: '',
      redirectPath: '',
      expires: [
        { description: 'For this browser session only', value: '' },
        { description: '1 day', value: '1' },
        { description: '3 days', value: '3' },
        { description: '7 days', value: '7' },
        { description: '14 days', value: '14' },
        { description: '30 days', value: '30' }
      ],
      valid: true,
      copied: false,
      maxOverrideLength: 50
    }
  },

  computed: {
    cookieBakerHost () {
      let host = {
        'chi.allegrogroup.com': 'allegro.pl'
      }[window.location.hostname]

      if (!host) {
        return 'allegro.pl.allegrosandbox.pl'
      }

      return host
    },

    sharingUrl () {
      let url = `https://${this.cookieBakerHost}/chi/cookie-baker.html?`

      if (this.chi) {
        url += `chi=${this.chi}&`
      }

      if (this.expire) {
        url += `expireDays=${this.expire}&`
      }

      let redirect = encodeURIComponent(`https://${this.cookieBakerHost}/${this.redirectPath}`)
      url += `redirect=${redirect}&`

      return url
    }
  },

  methods: {
    overrideValidator (v) {
      return v.length <= this.maxOverrideLength ||
        `Max ${this.maxOverrideLength} characters`
    },

    goAndSet () {
      window.open(this.sharingUrl, '_blank')
    },

    copyToClipboard () {
      this.$clipboard(this.sharingUrl)
      this.copied = true
    }
  }
}
</script>

