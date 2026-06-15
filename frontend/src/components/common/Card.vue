<template>
  <div class="card">
    <div v-if="title || $slots.header" class="card-header">
      <slot name="header">{{ title }}</slot>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
}>()
</script>