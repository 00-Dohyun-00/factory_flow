<template>
  <header class="header">
    <div class="header-content">
      <h1 class="header-title">{{ title }}</h1>
      <div class="header-actions">
        <span class="header-user">관리자</span>
        <div class="header-time">{{ currentTime }}</div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Props {
  title: string
}

defineProps<Props>()

const currentTime = ref('')
let timeInterval: number | null = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  updateTime()
  timeInterval = window.setInterval(updateTime, 60000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.header {
  height: 64px;
  background-color: var(--bg-primary);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  position: fixed;
  top: 0;
  left: 250px;
  right: 0;
  z-index: 50;
  padding: 0 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.header-user {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.header-time {
  font-size: 0.875rem;
  color: var(--text-secondary);
}
</style>