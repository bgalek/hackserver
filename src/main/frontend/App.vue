<template>
  <v-app>
    <v-navigation-drawer
      fixed
      v-model="drawer"
      :disable-resize-watcher="true"
      :disable-route-watcher="true"
      app
    >
      <v-list>
        <v-list-tile
          v-for="(item, i) in drawerItems"
          :key=i
          :to="{path: item.path}"
          v-if="!item.requireLogin || user.isLoggedIn"
        >
          <v-list-tile-action>
            <v-icon v-html="item.icon"></v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title v-text="item.title"></v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>

    <v-toolbar color="blue" dark fixed app>

      <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-btn href="/" icon>
        <v-avatar class="logo">
          <img src="./assets/logo.png"/>
        </v-avatar>
      </v-btn>
      <v-toolbar-title>Chi Admin</v-toolbar-title>
      <v-spacer></v-spacer>
        <v-btn href="/logout" flat style="text-transform: none">
          {{ user.name }}
          <v-icon>exit_to_app</v-icon>
        </v-btn>

    </v-toolbar>

    <v-content>
      <v-fade-transition mode="out-in">
        <router-view></router-view>
      </v-fade-transition>
    </v-content>

    <v-footer color="blue" app>
      <span class="white--text">&copy; {{ new Date().getFullYear() }}</span>
    </v-footer>
  </v-app>
</template>

<script>
  import { mapState, mapActions } from 'vuex'

  export default {
    created () {
      this.getUser()
    },

    computed: mapState({
      user: state => state.user && state.user.user
    }),

    data () {
      return {
        drawer: false,
        drawerItems: [
          { icon: 'bubble_chart', title: 'Experiments', path: '/experiments', requireLogin: false },
          { icon: 'visibility', title: 'Cookie Baker', path: '/cookie_baker', requireLogin: false },
          { icon: 'add', title: 'Create experiment', path: '/experiments/create', requireLogin: true }
        ]
      }
    },

    methods: {
      ...mapActions(['getUser'])
    }
  }
</script>

<style scoped>
.logo img {
  border-radius: 0;
}
</style>
