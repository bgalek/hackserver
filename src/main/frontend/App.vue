<template>
  <v-app>
    <v-navigation-drawer
      fixed
      v-model="drawer"
      disable-resize-watcher="True"
      disable-route-watcher="True"
      app
    >
      <v-list>
        <v-list-tile
          v-for="(item, i) in drawerItems"
          :key=i
          :to="{path: item.path}"
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

    <v-toolbar color="indigo" dark fixed app>
      <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-toolbar-title>Chi Admin</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-avatar class="logo">
        <img src="./assets/logo.png"></img>
      </v-avatar>
    </v-toolbar>

    <v-content>
      <v-fade-transition mode="out-in">
        <router-view></router-view>
      </v-fade-transition>
    </v-content>

    <v-footer color="indigo" app>
      <span class="white--text">&copy; 2017</span>
    </v-footer>
  </v-app>
</template>

<script>
  export default {
    data () {
      return {
        drawer: false,
        drawerItems: [
          { icon: 'bubble_chart', title: 'Experiments', path: '/experiments' },
          { icon: 'visibility', title: 'Cookie Baker', path: '/cookie_baker' }
        ]
      }
    }
  }
</script>

<style scoped>
.logo img {
  border-radius: 0;
}
</style>
