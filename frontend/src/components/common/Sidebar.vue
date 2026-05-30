<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <h2 class="sidebar-title">FactoryFlow</h2>
    </div>
    <nav class="sidebar-nav">
      <router-link 
        v-for="item in menuItems" 
        :key="item.path"
        :to="item.path" 
        class="sidebar-item"
        :class="{ 'sidebar-item-active': $route.path === item.path }"
      >
        <span class="sidebar-icon">{{ item.icon }}</span>
        <span class="sidebar-text">{{ item.name }}</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup lang="ts">
interface MenuItem {
  name: string
  path: string
  icon: string
}

const menuItems: MenuItem[] = [
  { name: 'Dashboard', path: '/', icon: '📊' },
  { name: 'Equipment Management', path: '/equipment', icon: '⚙️' },
  { name: 'Inventory Management', path: '/inventory', icon: '📦' },
  { name: 'Work Orders', path: '/work-orders', icon: '📋' },
  { name: 'Production Results', path: '/production-results', icon: '📈' },
  { name: 'Settings', path: '/settings', icon: '⚙️' },
]
</script>

<style scoped>
.sidebar {
  width: 250px;
  height: 100vh;
  background-color: var(--bg-primary);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.sidebar-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--primary-color);
}

.sidebar-nav {
  flex: 1;
  padding: 1rem 0;
}

.sidebar-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 1.5rem;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.15s ease-in-out;
}

.sidebar-item:hover {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.sidebar-item-active {
  background-color: var(--primary-color);
  color: white;
}

.sidebar-item-active:hover {
  background-color: var(--primary-hover);
}

.sidebar-icon {
  margin-right: 0.75rem;
  font-size: 1.125rem;
}

.sidebar-text {
  font-size: 0.875rem;
  font-weight: 500;
}
</style>