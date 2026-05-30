<template>
  <div class="equipment-page">
    <Card>
      <template #header>
        <div class="page-header">
          <h2>설비 관리</h2>
          <button class="btn btn-primary">
            <span>➕</span>
            설비 등록
          </button>
        </div>
      </template>
      
      <div class="filters">
        <div class="filter-row">
          <div class="filter-group">
            <label>설비 검색</label>
            <input 
              v-model="searchQuery"
              type="text" 
              class="input" 
              placeholder="설비코드, 설비명으로 검색"
            >
          </div>
          <div class="filter-group">
            <label>상태</label>
            <select v-model="statusFilter" class="select">
              <option value="">전체</option>
              <option value="active">가동</option>
              <option value="stopped">정지</option>
              <option value="maintenance">점검</option>
              <option value="error">이상</option>
            </select>
          </div>
          <div class="filter-actions">
            <button class="btn btn-secondary" @click="resetFilters">
              초기화
            </button>
            <button class="btn btn-primary" @click="searchEquipment">
              검색
            </button>
          </div>
        </div>
      </div>

      <DataTable 
        :columns="columns" 
        :data="filteredEquipment"
        :actions="true"
      >
        <template #cell-status="{ value }">
          <Badge :variant="getStatusVariant(value)">
            {{ getStatusText(value) }}
          </Badge>
        </template>
        
        <template #cell-lastCheckDate="{ value }">
          {{ formatDate(value) }}
        </template>
        
        <template #cell-registeredDate="{ value }">
          {{ formatDate(value) }}
        </template>

        <template #actions="{ row }">
          <div class="action-buttons">
            <button class="btn btn-sm btn-secondary" @click="editEquipment(row)">
              수정
            </button>
            <button class="btn btn-sm btn-secondary" @click="viewEquipment(row)">
              상세
            </button>
          </div>
        </template>
      </DataTable>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Card from '@/components/common/Card.vue'
import Badge from '@/components/common/Badge.vue'
import DataTable from '@/components/common/DataTable.vue'
import { ApiService } from '@/services/api'
import type { Equipment } from '@/types'

const equipmentList = ref<Equipment[]>([])
const searchQuery = ref('')
const statusFilter = ref('')

const columns = [
  { key: 'code', label: '설비코드' },
  { key: 'name', label: '설비명' },
  { key: 'location', label: '위치' },
  { key: 'status', label: '상태' },
  { key: 'manager', label: '담당자' },
  { key: 'lastCheckDate', label: '최근점검일' },
  { key: 'registeredDate', label: '등록일' }
]

const filteredEquipment = computed(() => {
  let filtered = equipmentList.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(equipment => 
      equipment.code.toLowerCase().includes(query) ||
      equipment.name.toLowerCase().includes(query)
    )
  }

  if (statusFilter.value) {
    filtered = filtered.filter(equipment => 
      equipment.status === statusFilter.value
    )
  }

  return filtered
})

const getStatusVariant = (status: string) => {
  switch (status) {
    case 'active': return 'success'
    case 'maintenance': return 'warning'
    case 'error': return 'error'
    case 'stopped': return 'secondary'
    default: return 'secondary'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'active': return '가동'
    case 'maintenance': return '점검'
    case 'error': return '이상'
    case 'stopped': return '정지'
    default: return status
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = ''
}

const searchEquipment = () => {
  console.log('검색 실행:', { searchQuery: searchQuery.value, statusFilter: statusFilter.value })
}

const editEquipment = (equipment: Equipment) => {
  console.log('설비 수정:', equipment)
}

const viewEquipment = (equipment: Equipment) => {
  console.log('설비 상세:', equipment)
}

const loadEquipmentList = async () => {
  try {
    equipmentList.value = await ApiService.getEquipmentList()
  } catch (error) {
    console.error('설비 목록 로드 실패:', error)
  }
}

onMounted(() => {
  loadEquipmentList()
})
</script>

<style scoped>
.equipment-page {
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
}
</style>