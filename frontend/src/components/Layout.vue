<template>
  <div class="layout">
    <Sidebar />
    <div class="main-content">
      <Header :title="pageTitle" />
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from '@/components/common/Sidebar.vue'
import Header from '@/components/common/Header.vue'

const route = useRoute()

const pageTitle = computed(() => {
  const titleMap: Record<string, string> = {
    'Dashboard': '대시보드',
    'Equipment': '설비 관리',
    'Inventory': '재고 관리', 
    'WorkOrders': '작업 지시',
    'ProductionResults': '생산 실적',
    'Settings': '설정'
  }
  return titleMap[route.name as string] || '대시보드'
})
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  margin-left: 250px;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  margin-top: 64px;
  padding: 2rem;
  background-color: var(--bg-secondary);
  min-height: calc(100vh - 64px);
}
</style>