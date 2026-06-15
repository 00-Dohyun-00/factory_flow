import type { Equipment, Material, WorkOrder, ProductionResult, DashboardKPI } from '@/types'
import { mockDashboardKPI } from './mockData'

const API_URL = import.meta.env.VITE_API_URL

const json = (method: string, body: unknown) => ({
  method,
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(body)
})

export class ApiService {
  static async getDashboardKPI(): Promise<DashboardKPI> {
    return mockDashboardKPI
  }

  // Equipment
  static async getEquipmentList(): Promise<Equipment[]> {
    const res = await fetch(`${API_URL}/api/equipment`)
    return res.json()
  }
  static async createEquipment(equipment: Equipment): Promise<Equipment> {
    const res = await fetch(`${API_URL}/api/equipment`, json('POST', equipment))
    return res.json()
  }
  static async updateEquipment(id: string, equipment: Equipment): Promise<Equipment> {
    const res = await fetch(`${API_URL}/api/equipment/${id}`, json('PUT', equipment))
    return res.json()
  }
  static async deleteEquipment(id: string): Promise<void> {
    await fetch(`${API_URL}/api/equipment/${id}`, { method: 'DELETE' })
  }

  // Material
  static async getMaterialList(): Promise<Material[]> {
    const res = await fetch(`${API_URL}/api/materials`)
    return res.json()
  }
  static async createMaterial(material: Material): Promise<Material> {
    const res = await fetch(`${API_URL}/api/materials`, json('POST', material))
    return res.json()
  }
  static async updateMaterial(id: string, material: Material): Promise<Material> {
    const res = await fetch(`${API_URL}/api/materials/${id}`, json('PUT', material))
    return res.json()
  }
  static async deleteMaterial(id: string): Promise<void> {
    await fetch(`${API_URL}/api/materials/${id}`, { method: 'DELETE' })
  }

  // WorkOrder
  static async getWorkOrderList(): Promise<WorkOrder[]> {
    const res = await fetch(`${API_URL}/api/workorders`)
    return res.json()
  }
  static async createWorkOrder(workOrder: WorkOrder): Promise<WorkOrder> {
    const res = await fetch(`${API_URL}/api/workorders`, json('POST', workOrder))
    return res.json()
  }
  static async updateWorkOrder(id: string, workOrder: WorkOrder): Promise<WorkOrder> {
    const res = await fetch(`${API_URL}/api/workorders/${id}`, json('PUT', workOrder))
    return res.json()
  }
  static async deleteWorkOrder(id: string): Promise<void> {
    await fetch(`${API_URL}/api/workorders/${id}`, { method: 'DELETE' })
  }

  // ProductionResult
  static async getProductionResultList(): Promise<ProductionResult[]> {
    const res = await fetch(`${API_URL}/api/production-results`)
    return res.json()
  }
  static async createProductionResult(result: ProductionResult): Promise<ProductionResult> {
    const res = await fetch(`${API_URL}/api/production-results`, json('POST', result))
    return res.json()
  }
  static async updateProductionResult(id: string, result: ProductionResult): Promise<ProductionResult> {
    const res = await fetch(`${API_URL}/api/production-results/${id}`, json('PUT', result))
    return res.json()
  }
  static async deleteProductionResult(id: string): Promise<void> {
    await fetch(`${API_URL}/api/production-results/${id}`, { method: 'DELETE' })
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
