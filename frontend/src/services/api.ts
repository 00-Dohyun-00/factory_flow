import type { Equipment, Material, WorkOrder, ProductionResult, DashboardKPI } from '@/types'
import { 
  mockEquipmentData, 
  mockMaterialData, 
  mockWorkOrderData, 
  mockProductionResultData, 
  mockDashboardKPI 
} from './mockData'

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export class ApiService {
  static async getDashboardKPI(): Promise<DashboardKPI> {
    await delay(300)
    return mockDashboardKPI
  }

  static async getEquipmentList(): Promise<Equipment[]> {
    await delay(500)
    return mockEquipmentData
  }

  static async getMaterialList(): Promise<Material[]> {
    await delay(500)
    return mockMaterialData
  }

  static async getWorkOrderList(): Promise<WorkOrder[]> {
    await delay(500)
    return mockWorkOrderData
  }

  static async getProductionResultList(): Promise<ProductionResult[]> {
    await delay(500)
    return mockProductionResultData
  }

  static async getRecentWorkOrders(limit: number = 5): Promise<WorkOrder[]> {
    await delay(300)
    return mockWorkOrderData.slice(0, limit)
  }

  static async getRecentProductionResults(limit: number = 5): Promise<ProductionResult[]> {
    await delay(300)
    return mockProductionResultData.slice(0, limit)
  }

  static async getLowStockMaterials(): Promise<Material[]> {
    await delay(300)
    return mockMaterialData.filter(material => material.status === 'low' || material.status === 'critical')
  }
}