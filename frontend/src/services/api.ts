import type { Equipment, Material, WorkOrder, ProductionResult, DashboardKPI } from '@/types'
import { mockDashboardKPI } from './mockData'

const API_URL = import.meta.env.VITE_API_URL

export class ApiService {
  static async getDashboardKPI(): Promise<DashboardKPI> {
    return mockDashboardKPI
  }

  static async getEquipmentList(): Promise<Equipment[]> {
    const res = await fetch(`${API_URL}/api/equipment`)
    return res.json()
  }

  static async getMaterialList(): Promise<Material[]> {
    const res = await fetch(`${API_URL}/api/materials`)
    return res.json()
  }

  static async getWorkOrderList(): Promise<WorkOrder[]> {
    const res = await fetch(`${API_URL}/api/workorders`)
    return res.json()
  }

  static async getProductionResultList(): Promise<ProductionResult[]> {
    const res = await fetch(`${API_URL}/api/production-results`)
    return res.json()
  }

  static async getRecentWorkOrders(limit: number = 5): Promise<WorkOrder[]> {
    const res = await fetch(`${API_URL}/api/workorders`)
    const workOrders: WorkOrder[] = await res.json()
    return workOrders.slice(0, limit)
  }

  static async getRecentProductionResults(limit: number = 5): Promise<ProductionResult[]> {
    const res = await fetch(`${API_URL}/api/production-results`)
    const results: ProductionResult[] = await res.json()
    return results.slice(0, limit)
  }

  static async getLowStockMaterials(): Promise<Material[]> {
    const res = await fetch(`${API_URL}/api/materials`)
    const materials: Material[] = await res.json()
    return materials.filter(material => material.status === 'low' || material.status === 'critical')
  }
}