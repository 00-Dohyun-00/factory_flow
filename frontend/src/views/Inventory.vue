<template>
  <div class="inventory-page">
    <Card>
      <template #header>
        <div class="page-header">
          <h2>재고 관리</h2>
          <div class="header-actions">
            <button class="btn btn-secondary">
              <span>📥</span>
              입고
            </button>
            <button class="btn btn-secondary">
              <span>📤</span>
              출고
            </button>
            <button class="btn btn-primary" @click="openCreateModal">
              <span>➕</span>
              자재 등록
            </button>
          </div>
        </div>
      </template>
      
      <div class="filters">
        <div class="filter-row">
          <div class="filter-group">
            <label>자재 검색</label>
            <input 
              v-model="searchQuery"
              type="text" 
              class="input" 
              placeholder="자재코드, 자재명으로 검색"
            >
          </div>
          <div class="filter-group">
            <label>카테고리</label>
            <select v-model="categoryFilter" class="select">
              <option value="">전체</option>
              <option value="원료">원료</option>
              <option value="포장재">포장재</option>
              <option value="부자재">부자재</option>
            </select>
          </div>
          <div class="filter-group">
            <label>상태</label>
            <select v-model="statusFilter" class="select">
              <option value="">전체</option>
              <option value="normal">정상</option>
              <option value="low">부족</option>
              <option value="critical">위험</option>
            </select>
          </div>
          <div class="filter-actions">
            <button class="btn btn-secondary" @click="resetFilters">
              초기화
            </button>
            <button class="btn btn-primary" @click="searchMaterials">
              검색
            </button>
          </div>
        </div>
      </div>

      <DataTable 
        :columns="columns" 
        :data="filteredMaterials"
        :actions="true"
      >
        <template #cell-currentStock="{ row }">
          <span :class="getStockClass(row)">
            {{ row.currentStock }} {{ row.unit }}
          </span>
        </template>

        <template #cell-safetyStock="{ row }">
          {{ row.safetyStock }} {{ row.unit }}
        </template>

        <template #cell-status="{ value }">
          <Badge :variant="getStatusVariant(value)">
            {{ getStatusText(value) }}
          </Badge>
        </template>
        
        <template #cell-lastMovementDate="{ value }">
          {{ formatDate(value) }}
        </template>

        <template #actions="{ row }">
          <div class="action-buttons">
            <button 
              class="btn btn-sm btn-success" 
              @click="adjustStock(row, 'in')"
            >
              입고
            </button>
            <button 
              class="btn btn-sm btn-warning" 
              @click="adjustStock(row, 'out')"
            >
              출고
            </button>
            <button class="btn btn-sm btn-secondary" @click="editMaterial(row)">
              수정
            </button>
            <button class="btn btn-sm btn-secondary" @click="viewMaterial(row)">
              상세
            </button>
          </div>
        </template>
      </DataTable>
    </Card>

    <!-- 자재 상세 모달 -->
    <MaterialDetailModal
      v-model="showDetailModal"
      :material="viewingMaterial"
      @edit="handleDetailEdit"
      @stock-in="handleDetailStockIn"
      @stock-out="handleDetailStockOut"
    />

    <!-- 자재 등록/수정 모달 -->
    <MaterialFormModal
      v-model="showFormModal"
      :material="editingMaterial"
      @submit="handleFormSubmit"
    />

    <!-- 재고 입고/출고 모달 -->
    <StockAdjustmentModal
      v-model="showStockModal"
      :material="viewingMaterial"
      :type="stockAdjustmentType"
      @submit="handleStockAdjustment"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Card from '@/components/common/Card.vue'
import Badge from '@/components/common/Badge.vue'
import DataTable from '@/components/common/DataTable.vue'
import MaterialDetailModal from '@/components/inventory/MaterialDetailModal.vue'
import MaterialFormModal from '@/components/inventory/MaterialFormModal.vue'
import StockAdjustmentModal from '@/components/inventory/StockAdjustmentModal.vue'
import { ApiService } from '@/services/api'
import type { Material } from '@/types'

const materialList = ref<Material[]>([])
const searchQuery = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')

const showDetailModal = ref(false)
const showFormModal = ref(false)
const showStockModal = ref(false)
const editingMaterial = ref<Material | null>(null)
const viewingMaterial = ref<Material | null>(null)
const stockAdjustmentType = ref<'in' | 'out'>('in')

const columns = [
  { key: 'code', label: '자재코드' },
  { key: 'name', label: '자재명' },
  { key: 'category', label: '카테고리' },
  { key: 'currentStock', label: '현재수량' },
  { key: 'safetyStock', label: '안전재고' },
  { key: 'status', label: '상태' },
  { key: 'lastMovementDate', label: '최근입출고일' }
]

const filteredMaterials = computed(() => {
  let filtered = materialList.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(material => 
      material.code.toLowerCase().includes(query) ||
      material.name.toLowerCase().includes(query)
    )
  }

  if (categoryFilter.value) {
    filtered = filtered.filter(material => 
      material.category === categoryFilter.value
    )
  }

  if (statusFilter.value) {
    filtered = filtered.filter(material => 
      material.status === statusFilter.value
    )
  }

  return filtered
})

const getStockClass = (material: Material) => {
  if (material.status === 'critical') return 'stock-critical'
  if (material.status === 'low') return 'stock-low'
  return 'stock-normal'
}

const getStatusVariant = (status: string) => {
  switch (status) {
    case 'normal': return 'success'
    case 'low': return 'warning'
    case 'critical': return 'error'
    default: return 'secondary'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'normal': return '정상'
    case 'low': return '부족'
    case 'critical': return '위험'
    default: return status
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const resetFilters = () => {
  searchQuery.value = ''
  categoryFilter.value = ''
  statusFilter.value = ''
}

const searchMaterials = () => {
  console.log('검색 실행:', { 
    searchQuery: searchQuery.value, 
    categoryFilter: categoryFilter.value,
    statusFilter: statusFilter.value 
  })
}

const openCreateModal = () => {
  editingMaterial.value = null
  showFormModal.value = true
}

const adjustStock = (material: Material, type: 'in' | 'out') => {
  viewingMaterial.value = material
  stockAdjustmentType.value = type
  showStockModal.value = true
}

const editMaterial = (material: Material) => {
  editingMaterial.value = material
  showFormModal.value = true
}

const viewMaterial = (material: Material) => {
  viewingMaterial.value = material
  showDetailModal.value = true
}

const handleDetailEdit = (material: Material) => {
  showDetailModal.value = false
  editingMaterial.value = material
  showFormModal.value = true
}

const handleDetailStockIn = (material: Material) => {
  showDetailModal.value = false
  adjustStock(material, 'in')
}

const handleDetailStockOut = (material: Material) => {
  showDetailModal.value = false
  adjustStock(material, 'out')
}

const handleFormSubmit = async (formData: Partial<Material>) => {
  if (editingMaterial.value) {
    await ApiService.updateMaterial(editingMaterial.value.id, formData as Material)
  } else {
    await ApiService.createMaterial(formData as Material)
  }
  await loadMaterialList()
}

const handleStockAdjustment = async (data: any) => {
  const material = data.material
  const newStock = data.type === 'in'
    ? material.currentStock + data.quantity
    : material.currentStock - data.quantity

  const newStatus = getStatus(newStock, material.safetyStock)

  await ApiService.updateMaterial(material.id, {
    ...material,
    currentStock: newStock,
    status: newStatus,
    lastMovementDate: new Date().toISOString().split('T')[0]
  })
  await loadMaterialList()
}

const getStatus = (currentStock: number, safetyStock: number): Material['status'] => {
  if (currentStock === 0) return 'critical'
  if (currentStock <= safetyStock * 0.5) return 'critical'
  if (currentStock <= safetyStock) return 'low'
  return 'normal'
}

const loadMaterialList = async () => {
  try {
    materialList.value = await ApiService.getMaterialList()
  } catch (error) {
    console.error('자재 목록 로드 실패:', error)
  }
}

onMounted(() => {
  loadMaterialList()
})
</script>

<style scoped>
.inventory-page {
  max-width: 1400px;
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

.header-actions {
  display: flex;
  gap: 0.5rem;
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
  min-width: 180px;
}

.filter-actions {
  display: flex;
  gap: 0.5rem;
  margin-left: auto;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.stock-normal {
  color: var(--success-color);
  font-weight: 500;
}

.stock-low {
  color: var(--warning-color);
  font-weight: 500;
}

.stock-critical {
  color: var(--error-color);
  font-weight: 700;
}

.btn-success {
  background-color: var(--success-color);
  color: white;
}

.btn-success:hover {
  background-color: #059669;
}

.btn-warning {
  background-color: var(--warning-color);
  color: white;
}

.btn-warning:hover {
  background-color: #d97706;
}
</style>