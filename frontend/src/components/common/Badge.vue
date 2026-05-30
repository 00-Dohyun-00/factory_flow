<template>
  <span :class="badgeClasses">
    <slot></slot>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'success' | 'warning' | 'error' | 'info' | 'secondary'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'secondary'
})

const badgeClasses = computed(() => [
  'badge',
  `badge-${props.variant}`
])
</script>