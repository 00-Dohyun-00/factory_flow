import type { Equipment, Material, WorkOrder, ProductionResult, DashboardKPI } from '@/types'

export const mockEquipmentData: Equipment[] = [
  {
    id: '1',
    code: 'EQ001',
    name: '사출성형기 A',
    location: '1공장 A라인',
    status: 'active',
    manager: '김철수',
    lastCheckDate: '2024-05-25',
    registeredDate: '2023-01-15'
  },
  {
    id: '2',
    code: 'EQ002',
    name: '포장기계 B',
    location: '1공장 B라인',
    status: 'maintenance',
    manager: '이영희',
    lastCheckDate: '2024-05-20',
    registeredDate: '2023-02-20'
  },
  {
    id: '3',
    code: 'EQ003',
    name: '절단기 C',
    location: '2공장 A라인',
    status: 'error',
    manager: '박민수',
    lastCheckDate: '2024-05-28',
    registeredDate: '2023-03-10'
  },
  {
    id: '4',
    code: 'EQ004',
    name: '검사장비 D',
    location: '2공장 B라인',
    status: 'stopped',
    manager: '최수진',
    lastCheckDate: '2024-05-27',
    registeredDate: '2023-04-05'
  }
]

export const mockMaterialData: Material[] = [
  {
    id: '1',
    code: 'MT001',
    name: 'ABS 수지',
    category: '원료',
    currentStock: 150,
    safetyStock: 100,
    unit: 'kg',
    status: 'normal',
    lastMovementDate: '2024-05-28'
  },
  {
    id: '2',
    code: 'MT002',
    name: '포장박스',
    category: '포장재',
    currentStock: 50,
    safetyStock: 200,
    unit: '개',
    status: 'critical',
    lastMovementDate: '2024-05-25'
  },
  {
    id: '3',
    code: 'MT003',
    name: '라벨지',
    category: '부자재',
    currentStock: 80,
    safetyStock: 100,
    unit: 'roll',
    status: 'low',
    lastMovementDate: '2024-05-27'
  },
  {
    id: '4',
    code: 'MT004',
    name: '스테인리스 강판',
    category: '원료',
    currentStock: 300,
    safetyStock: 150,
    unit: 'kg',
    status: 'normal',
    lastMovementDate: '2024-05-29'
  }
]

export const mockWorkOrderData: WorkOrder[] = [
  {
    id: '1',
    orderNumber: 'WO2024001',
    productName: '플라스틱 부품 A',
    targetQuantity: 1000,
    completedQuantity: 750,
    equipment: 'EQ001',
    status: 'in_progress',
    startDate: '2024-05-25',
    dueDate: '2024-06-01'
  },
  {
    id: '2',
    orderNumber: 'WO2024002',
    productName: '포장박스 세트',
    targetQuantity: 500,
    completedQuantity: 500,
    equipment: 'EQ002',
    status: 'completed',
    startDate: '2024-05-20',
    dueDate: '2024-05-28'
  },
  {
    id: '3',
    orderNumber: 'WO2024003',
    productName: '금속 부품 B',
    targetQuantity: 200,
    completedQuantity: 0,
    equipment: 'EQ003',
    status: 'waiting',
    startDate: '2024-06-01',
    dueDate: '2024-06-10'
  },
  {
    id: '4',
    orderNumber: 'WO2024004',
    productName: '검사완료 제품',
    targetQuantity: 300,
    completedQuantity: 0,
    equipment: 'EQ004',
    status: 'cancelled',
    startDate: '2024-05-30',
    dueDate: '2024-06-05'
  }
]

export const mockProductionResultData: ProductionResult[] = [
  {
    id: '1',
    resultNumber: 'PR2024001',
    orderNumber: 'WO2024001',
    productName: '플라스틱 부품 A',
    productionQuantity: 250,
    defectQuantity: 5,
    yieldRate: 98.0,
    equipment: 'EQ001',
    workDate: '2024-05-29'
  },
  {
    id: '2',
    resultNumber: 'PR2024002',
    orderNumber: 'WO2024002',
    productName: '포장박스 세트',
    productionQuantity: 500,
    defectQuantity: 10,
    yieldRate: 98.0,
    equipment: 'EQ002',
    workDate: '2024-05-28'
  },
  {
    id: '3',
    resultNumber: 'PR2024003',
    orderNumber: 'WO2024001',
    productName: '플라스틱 부품 A',
    productionQuantity: 300,
    defectQuantity: 8,
    yieldRate: 97.3,
    equipment: 'EQ001',
    workDate: '2024-05-27'
  },
  {
    id: '4',
    resultNumber: 'PR2024004',
    orderNumber: 'WO2024001',
    productName: '플라스틱 부품 A',
    productionQuantity: 200,
    defectQuantity: 2,
    yieldRate: 99.0,
    equipment: 'EQ001',
    workDate: '2024-05-26'
  }
]

export const mockDashboardKPI: DashboardKPI = {
  todayProduction: 250,
  activeWorks: 1,
  equipmentErrors: 1,
  lowStockItems: 2
}