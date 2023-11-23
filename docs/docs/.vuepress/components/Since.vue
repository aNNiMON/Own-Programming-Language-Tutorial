<template>
  <span class="since">{{ text }} <b>{{ version }}</b></span>
</template>

<script>
export default {
  props: {
    text: {
      type: String,
      required: false,
      default: ''
    },
    version: {
      type: String,
      required: true
    }
  }
}
</script>

<style scoped>
.since {
  display: inline-block;
  font-size: 13px;
  height: 18px;
  line-height: 18px;
  border-radius: 3px;
  padding: 0 4px;
  color: var(--c-bg);
  background-color: var(--usc-since);
}
</style>