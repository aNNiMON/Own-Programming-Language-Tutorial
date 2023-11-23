<template>
  <span
    class="badge scope"
    :class="scope"
  >
    <slot>{{ scope }}</slot>
  </span>
</template>

<script setup>
defineProps({
  scope: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.badge.scope {
  vertical-align: middle;
  text-transform: capitalize;
}
.badge.android {
  background-color: var(--c-badge-tip);
}
.badge.desktop {
  background-color: var(--usc-scope);
}
</style>