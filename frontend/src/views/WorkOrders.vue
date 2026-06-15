<template>
  <div class="work-orders-page">
    <Card>
      <template #header>
        <div class="page-header">
          <h2>작업 지시</h2>
          <button class="btn btn-primary" @click="openCreateModal">
            <Plus :size="16" />
            작업 등록
          </button>
        </div>
      </template>
      
      <div class="filters">
        <div class="filter-row">
          <div class="filter-group">
            <label>작업지시 검색</label>
            <input 
              v-model="searchQuery"
              type="text" 
              class="input" 
              placeholder="작업지시 번호, 제품명으로 검색"
            >
          </div>
          <div class="filter-group">
            <label>상태</label>
            <select v-model="statusFilter" class="select">
              <option value="">전체</option>
              <option value="waiting">대기</option>
              <option value="in_progress">진행중</option>
              <option value="completed">완료</option>
              <option value="cancelled">취소</option>
            </select>
          </div>
          <div class="filter-group">
            <label>설비</label>
            <select v-model="equipmentFilter" class="select">
              <option value="">전체 설비</option>
              <option value="EQ001">EQ001 - 사출성형기 A</option>
              <option value="EQ002">EQ002 - 포장기계 B</option>
              <option value="EQ003">EQ003 - 절단기 C</option>
              <option value="EQ004">EQ004 - 검사장비 D</option>
            </select>
          </div>
          <div class="filter-actions">
            <button class="btn btn-secondary" @click="resetFilters">
              초기화
            </button>
            <button class="btn btn-primary" @click="searchWorkOrders">
              검색
            </button>
          </div>
        </div>
      </div>

      <DataTable 
        :columns="columns" 
        :data="filteredWorkOrders"
        :actions="true"
      >
        <template #cell-targetQuantity="{ value }">
          {{ value.toLocaleString() }}
        </template>

        <template #cell-completedQuantity="{ value }">
          {{ value.toLocaleString() }}
        </template>

        <template #cell-progress="{ row }">
          <div class="progress-container">
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: getProgressPercentage(row) + '%' }"
              ></div>
            </div>
            <span class="progress-text">{{ getProgressPercentage(row) }}%</span>
          </div>
        </template>

        <template #cell-status="{ value }">
          <Badge :variant="getStatusVariant(value)">
            {{ getStatusText(value) }}
          </Badge>
        </template>
        
        <template #cell-startDate="{ value }">
          {{ formatDate(value) }}
        </template>
        
        <template #cell-dueDate="{ value }">
          <span :class="getDueDateClass(value)">
            {{ formatDate(value) }}
          </span>
        </template>

        <template #actions="{ row }">
          <div class="action-buttons">
            <button 
              v-if="row.status === 'waiting'"
              class="btn btn-sm btn-primary" 
              @click="startWork(row)"
            >
              시작
            </button>
            <button 
              v-if="row.status === 'in_progress'"
              class="btn btn-sm btn-warning" 
              @click="pauseWork(row)"
            >
              일시정지
            </button>
            <button 
              v-if="row.status === 'in_progress'"
              class="btn btn-sm btn-success" 
              @click="completeWork(row)"
            >
              완료
            </button>
            <button class="btn btn-sm btn-secondary" @click="editWork(row)">수정</button>
            <button class="btn btn-sm btn-secondary" @click="viewWork(row)">상세</button>
            <button class="btn btn-sm btn-danger" @click="deleteWorkOrder(row)">
              <Trash2 :size="14" />
            </button>
          </div>
        </template>
      </DataTable>
    </Card>

    <!-- 작업지시 상세 모달 -->
    <WorkOrderDetailModal
      v-model="showDetailModal"
      :work-order="viewingWorkOrder"
      @edit="handleDetailEdit"
      @start="handleDetailStart"
      @pause="handleDetailPause"
      @complete="handleDetailComplete"
    />

    <!-- 작업지시 등록/수정 모달 -->
    <WorkOrderFormModal
      v-model="showFormModal"
      :work-order="editingWorkOrder"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Plus, Trash2 } from '@lucide/vue'
import Card from '@/components/common/Card.vue'
import Badge from '@/components/common/Badge.vue'
import DataTable from '@/components/common/DataTable.vue'
import WorkOrderDetailModal from '@/components/workorder/WorkOrderDetailModal.vue'
import WorkOrderFormModal from '@/components/workorder/WorkOrderFormModal.vue'
import { ApiService } from '@/services/api'
import type { WorkOrder } from '@/types'

const workOrderList = ref<WorkOrder[]>([])
const searchQuery = ref('')
const statusFilter = ref('')
const equipmentFilter = ref('')

const showDetailModal = ref(false)
const showFormModal = ref(false)
const editingWorkOrder = ref<WorkOrder | null>(null)
const viewingWorkOrder = ref<WorkOrder | null>(null)

const columns = [
  { key: 'orderNumber', label: '작업지시번호' },
  { key: 'productName', label: '제품명' },
  { key: 'targetQuantity', label: '목표수량' },
  { key: 'completedQuantity', label: '완료수량' },
  { key: 'progress', label: '진행률' },
  { key: 'equipment', label: '담당설비' },
  { key: 'status', label: '상태' },
  { key: 'startDate', label: '시작예정일' },
  { key: 'dueDate', label: '납기일' }
]

const filteredWorkOrders = computed(() => {
  let filtered = workOrderList.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(order => 
      order.orderNumber.toLowerCase().includes(query) ||
      order.productName.toLowerCase().includes(query)
    )
  }

  if (statusFilter.value) {
    filtered = filtered.filter(order => 
      order.status === statusFilter.value
    )
  }

  if (equipmentFilter.value) {
    filtered = filtered.filter(order => 
      order.equipment === equipmentFilter.value
    )
  }

  return filtered
})

const getProgressPercentage = (workOrder: WorkOrder) => {
  if (workOrder.targetQuantity === 0) return 0
  return Math.round((workOrder.completedQuantity / workOrder.targetQuantity) * 100)
}

const getStatusVariant = (status: string) => {
  switch (status) {
    case 'completed': return 'success'
    case 'in_progress': return 'info'
    case 'waiting': return 'secondary'
    case 'cancelled': return 'error'
    default: return 'secondary'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'completed': return '완료'
    case 'in_progress': return '진행중'
    case 'waiting': return '대기'
    case 'cancelled': return '취소'
    default: return status
  }
}

const getDueDateClass = (dueDate: string) => {
  const today = new Date()
  const due = new Date(dueDate)
  const diffDays = Math.ceil((due.getTime() - today.getTime()) / (1000 * 3600 * 24))
  
  if (diffDays < 0) return 'overdue'
  if (diffDays <= 3) return 'urgent'
  return 'normal'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  equipmentFilter.value = ''
}

const searchWorkOrders = () => {
  console.log('검색 실행:', { 
    searchQuery: searchQuery.value, 
    statusFilter: statusFilter.value,
    equipmentFilter: equipmentFilter.value 
  })
}

const openCreateModal = () => {
  editingWorkOrder.value = null
  showFormModal.value = true
}

const startWork = (workOrder: WorkOrder) => {
  updateWorkOrderStatus(workOrder, 'in_progress')
  console.log('작업 시작:', workOrder)
}

const pauseWork = (workOrder: WorkOrder) => {
  updateWorkOrderStatus(workOrder, 'waiting')
  console.log('작업 일시정지:', workOrder)
}

const completeWork = async (workOrder: WorkOrder) => {
  await ApiService.updateWorkOrder(workOrder.id, {
    ...workOrder,
    status: 'completed',
    completedQuantity: workOrder.targetQuantity
  })
  await loadWorkOrderList()
}

const editWork = (workOrder: WorkOrder) => {
  editingWorkOrder.value = workOrder
  showFormModal.value = true
}

const viewWork = (workOrder: WorkOrder) => {
  viewingWorkOrder.value = workOrder
  showDetailModal.value = true
}

const handleDetailEdit = (workOrder: WorkOrder) => {
  showDetailModal.value = false
  editingWorkOrder.value = workOrder
  showFormModal.value = true
}

const handleDetailStart = (workOrder: WorkOrder) => {
  showDetailModal.value = false
  startWork(workOrder)
}

const handleDetailPause = (workOrder: WorkOrder) => {
  showDetailModal.value = false
  pauseWork(workOrder)
}

const handleDetailComplete = (workOrder: WorkOrder) => {
  showDetailModal.value = false
  completeWork(workOrder)
}

const handleFormSubmit = async (formData: Partial<WorkOrder>) => {
  if (editingWorkOrder.value) {
    await ApiService.updateWorkOrder(editingWorkOrder.value.id, formData as WorkOrder)
  } else {
    await ApiService.createWorkOrder(formData as WorkOrder)
  }
  await loadWorkOrderList()
}

const updateWorkOrderStatus = async (workOrder: WorkOrder, status: WorkOrder['status']) => {
  await ApiService.updateWorkOrder(workOrder.id, { ...workOrder, status })
  await loadWorkOrderList()
}

const deleteWorkOrder = async (workOrder: WorkOrder) => {
  if (!confirm(`"${workOrder.orderNumber}"을 삭제하시겠습니까?`)) return
  await ApiService.deleteWorkOrder(workOrder.id)
  await loadWorkOrderList()
}

const loadWorkOrderList = async () => {
  try {
    workOrderList.value = await ApiService.getWorkOrderList()
  } catch (error) {
    console.error('작업지시 목록 로드 실패:', error)
  }
}

onMounted(() => {
  loadWorkOrderList()
})
</script>

<style scoped>
.work-orders-page {
  max-width: 1600px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.page-header h2 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.filters {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.filter-row {
  display: flex;
  gap: 1rem;
  align-items: end;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-primary);
}

.filter-group .input,
.filter-group .select {
  min-width: 200px;
}

.filter-actions {
  display: flex;
  gap: 0.5rem;
  margin-left: auto;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 120px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: var(--bg-tertiary);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: var(--primary-color);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-secondary);
  min-width: 35px;
}

.overdue {
  color: var(--error-color);
  font-weight: 600;
}

.urgent {
  color: var(--warning-color);
  font-weight: 500;
}

.normal {
  color: var(--text-primary);
}

.btn-warning {
  background-color: var(--warning-color);
  color: white;
}

.btn-warning:hover {
  background-color: #d97706;
}

.btn-success {
  background-color: var(--success-color);
  color: white;
}

.btn-success:hover {
  background-color: #059669;
}
</style>