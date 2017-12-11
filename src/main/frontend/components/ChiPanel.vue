<template>
  <div class="mt-3">
    <v-toolbar color="blue">
      <v-toolbar-title class="white--text">{{title}}</v-toolbar-title>
    </v-toolbar>
    <v-card tile="true" style="padding:10px;">
      <slot>
      </slot>

      <div class="mt-1">
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
