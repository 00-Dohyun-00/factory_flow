<template>
  <div class="production-results-page">
    <Card>
      <template #header>
        <div class="page-header">
          <h2>생산 실적</h2>
          <div class="header-actions">
            <button class="btn btn-primary" @click="openCreateModal">
              <Plus :size="16" />
              실적 등록
            </button>
            <button class="btn btn-secondary">
              <span>📊</span>
              실적 분석
            </button>
            <button class="btn btn-secondary">
              <span>📥</span>
              Excel 내보내기
            </button>
          </div>
        </div>
      </template>
      
      <div class="filters">
        <div class="filter-row">
          <div class="filter-group">
            <label>실적 검색</label>
            <input 
              v-model="searchQuery"
              type="text" 
              class="input" 
              placeholder="실적번호, 작업지시번호, 제품명으로 검색"
            >
          </div>
          <div class="filter-group">
            <label>작업일 (시작)</label>
            <input 
              v-model="startDate"
              type="date" 
              class="input"
            >
          </div>
          <div class="filter-group">
            <label>작업일 (종료)</label>
            <input 
              v-model="endDate"
              type="date" 
              class="input"
            >
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
            <button class="btn btn-primary" @click="searchResults">
              검색
            </button>
          </div>
        </div>
      </div>

      <div class="summary-cards">
        <div class="summary-card">
          <div class="summary-label">총 생산량</div>
          <div class="summary-value">{{ totalProduction.toLocaleString() }}</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">총 불량량</div>
          <div class="summary-value error">{{ totalDefects.toLocaleString() }}</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">평균 수율</div>
          <div class="summary-value" :class="getYieldClass(averageYield)">{{ averageYield.toFixed(1) }}%</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">총 실적 건수</div>
          <div class="summary-value">{{ filteredResults.length }}</div>
        </div>
      </div>

      <DataTable 
        :columns="columns" 
        :data="filteredResults"
        :actions="true"
      >
        <template #cell-productionQuantity="{ value }">
          {{ value.toLocaleString() }}
        </template>

        <template #cell-defectQuantity="{ value }">
          <span class="defect-quantity">{{ value.toLocaleString() }}</span>
        </template>

        <template #cell-yieldRate="{ value }">
          <span :class="getYieldClass(value)">{{ value }}%</span>
        </template>
        
        <template #cell-workDate="{ value }">
          {{ formatDate(value) }}
        </template>

        <template #actions="{ row }">
          <div class="action-buttons">
            <button class="btn btn-sm btn-secondary" @click="viewDetails(row)">상세보기</button>
            <button class="btn btn-sm btn-secondary" @click="editResult(row)">수정</button>
            <button class="btn btn-sm btn-danger" @click="deleteResult(row)">
              <Trash2 :size="14" />
            </button>
          </div>
        </template>
      </DataTable>
    </Card>

    <ProductionResultDetailModal
      v-model="showDetailModal"
      :production-result="viewingResult"
      @edit="handleDetailEdit"
    />

    <ProductionResultFormModal
      v-model="showFormModal"
      :production-result="editingResult"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Plus, Trash2 } from '@lucide/vue'
import Card from '@/components/common/Card.vue'
import DataTable from '@/components/common/DataTable.vue'
import ProductionResultDetailModal from '@/components/productionresult/ProductionResultDetailModal.vue'
import ProductionResultFormModal from '@/components/productionresult/ProductionResultFormModal.vue'
import { ApiService } from '@/services/api'
import type { ProductionResult } from '@/types'

const productionResults = ref<ProductionResult[]>([])
const searchQuery = ref('')
const startDate = ref('')
const endDate = ref('')
const equipmentFilter = ref('')

const showDetailModal = ref(false)
const showFormModal = ref(false)
const viewingResult = ref<ProductionResult | null>(null)
const editingResult = ref<ProductionResult | null>(null)

const columns = [
  { key: 'resultNumber', label: '실적번호' },
  { key: 'orderNumber', label: '작업지시번호' },
  { key: 'productName', label: '제품명' },
  { key: 'productionQuantity', label: '생산수량' },
  { key: 'defectQuantity', label: '불량수량' },
  { key: 'yieldRate', label: '수율' },
  { key: 'equipment', label: '사용설비' },
  { key: 'workDate', label: '작업일' }
]

const filteredResults = computed(() => {
  let filtered = productionResults.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(result => 
      result.resultNumber.toLowerCase().includes(query) ||
      result.orderNumber.toLowerCase().includes(query) ||
      result.productName.toLowerCase().includes(query)
    )
  }

  if (startDate.value) {
    filtered = filtered.filter(result => 
      new Date(result.workDate) >= new Date(startDate.value)
    )
  }

  if (endDate.value) {
    filtered = filtered.filter(result => 
      new Date(result.workDate) <= new Date(endDate.value)
    )
  }

  if (equipmentFilter.value) {
    filtered = filtered.filter(result => 
      result.equipment === equipmentFilter.value
    )
  }

  return filtered
})

const totalProduction = computed(() => {
  return filteredResults.value.reduce((sum, result) => sum + result.productionQuantity, 0)
})

const totalDefects = computed(() => {
  return filteredResults.value.reduce((sum, result) => sum + result.defectQuantity, 0)
})

const averageYield = computed(() => {
  if (filteredResults.value.length === 0) return 0
  const totalYield = filteredResults.value.reduce((sum, result) => sum + result.yieldRate, 0)
  return totalYield / filteredResults.value.length
})

const getYieldClass = (yieldValue: number) => {
  if (yieldValue >= 98) return 'yield-excellent'
  if (yieldValue >= 95) return 'yield-good'
  if (yieldValue >= 90) return 'yield-warning'
  return 'yield-poor'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const resetFilters = () => {
  searchQuery.value = ''
  startDate.value = ''
  endDate.value = ''
  equipmentFilter.value = ''
}

const searchResults = () => {
  console.log('검색 실행:', { 
    searchQuery: searchQuery.value,
    startDate: startDate.value,
    endDate: endDate.value,
    equipmentFilter: equipmentFilter.value 
  })
}

const viewDetails = (result: ProductionResult) => {
  viewingResult.value = result
  showDetailModal.value = true
}

const editResult = (result: ProductionResult) => {
  editingResult.value = result
  showFormModal.value = true
}

const handleDetailEdit = (result: ProductionResult) => {
  showDetailModal.value = false
  editingResult.value = result
  showFormModal.value = true
}

const handleFormSubmit = async (formData: Partial<ProductionResult>) => {
  if (editingResult.value) {
    await ApiService.updateProductionResult(editingResult.value.id, formData as ProductionResult)
  } else {
    await ApiService.createProductionResult(formData as ProductionResult)
  }
  await loadProductionResults()
}

const printReport = (result: ProductionResult) => {
  console.log('실적 출력:', result)
}

const openCreateModal = () => {
  editingResult.value = null
  showFormModal.value = true
}

const deleteResult = async (result: ProductionResult) => {
  if (!confirm(`"${result.resultNumber}"을 삭제하시겠습니까?`)) return
  await ApiService.deleteProductionResult(result.id)
  await loadProductionResults()
}

const loadProductionResults = async () => {
  try {
    productionResults.value = await ApiService.getProductionResultList()
  } catch (error) {
    console.error('생산실적 목록 로드 실패:', error)
  }
}

onMounted(() => {
  loadProductionResults()
})
</script>

<style scoped>
.production-results-page {
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
  min-width: 160px;
}

.filter-actions {
  display: flex;
  gap: 0.5rem;
  margin-left: auto;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.summary-card {
  background-color: var(--bg-secondary);
  padding: 1rem;
  border-radius: 0.5rem;
  text-align: center;
}

.summary-label {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.summary-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
}

.summary-value.error {
  color: var(--error-color);
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.defect-quantity {
  color: var(--error-color);
  font-weight: 500;
}

.yield-excellent {
  color: var(--success-color);
  font-weight: 600;
}

.yield-good {
  color: #22c55e;
  font-weight: 500;
}

.yield-warning {
  color: var(--warning-color);
  font-weight: 500;
}

.yield-poor {
  color: var(--error-color);
  font-weight: 600;
}
</style>