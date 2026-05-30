export interface Equipment {
  id: string
  code: string
  name: string
  location: string
  status: 'active' | 'stopped' | 'maintenance' | 'error'
  manager: string
  lastCheckDate: string
  registeredDate: string
}

export interface Material {
  id: string
  code: string
  name: string
  category: string
  currentStock: number
  safetyStock: number
  unit: string
  status: 'normal' | 'low' | 'critical'
  lastMovementDate: string
}

export interface WorkOrder {
  id: string
  orderNumber: string
  productName: string
  targetQuantity: number
  completedQuantity: number
  equipment: string
  status: 'waiting' | 'in_progress' | 'completed' | 'cancelled'
  startDate: string
  dueDate: string
}

export interface ProductionResult {
  id: string
  resultNumber: string
  orderNumber: string
  productName: string
  productionQuantity: number
  defectQuantity: number
  yieldRate: number
  equipment: string
  workDate: string
}

export interface DashboardKPI {
  todayProduction: number
  activeWorks: number
  equipmentErrors: number
  lowStockItems: number
}