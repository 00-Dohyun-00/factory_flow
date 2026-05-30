import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'
import Equipment from '@/views/Equipment.vue'
import Inventory from '@/views/Inventory.vue'
import WorkOrders from '@/views/WorkOrders.vue'
import ProductionResults from '@/views/ProductionResults.vue'
import Settings from '@/views/Settings.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Dashboard',
      component: Dashboard
    },
    {
      path: '/equipment',
      name: 'Equipment',
      component: Equipment
    },
    {
      path: '/inventory',
      name: 'Inventory',
      component: Inventory
    },
    {
      path: '/work-orders',
      name: 'WorkOrders',
      component: WorkOrders
    },
    {
      path: '/production-results',
      name: 'ProductionResults',
      component: ProductionResults
    },
    {
      path: '/settings',
      name: 'Settings',
      component: Settings
    }
  ]
})

export default router