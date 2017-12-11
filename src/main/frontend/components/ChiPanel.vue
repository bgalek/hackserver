<template>
  <div style="margin-top: 15px;">
    <v-toolbar color="blue">
      <v-toolbar-title class="white--text">{{title}}</v-toolbar-title>
    </v-toolbar>
    <v-card tile="true" style="padding:10px;">
      <slot>
      </slot>

      <div style="margin-top: 5px;">
        <slot name="footer">
        </slot>
      </div>
    </v-card>
  </div>
</template>
<script>

export default {
  props: ['title']
}
</script>
