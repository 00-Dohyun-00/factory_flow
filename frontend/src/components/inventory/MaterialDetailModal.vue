<template>
  <Modal 
    :model-value="modelValue" 
    title="재고 상세 정보"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div v-if="material" class="detail-container">
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">자재코드</div>
          <div class="detail-value">{{ material.code }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">자재명</div>
          <div class="detail-value">{{ material.name }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">카테고리</div>
          <div class="detail-value">{{ material.category }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">현재 수량</div>
          <div class="detail-value" :class="getStockClass(material)">
            {{ material.currentStock }} {{ material.unit }}
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-label">안전 재고</div>
          <div class="detail-value">{{ material.safetyStock }} {{ material.unit }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">상태</div>
          <div class="detail-value">
            <Badge :variant="getStatusVariant(material.status)">
              {{ getStatusText(material.status) }}
            </Badge>
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-label">최근 입출고일</div>
          <div class="detail-value">{{ formatDate(material.lastMovementDate) }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">재고 회전율</div>
          <div class="detail-value">{{ getStockTurnover() }}회/월</div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">최근 입출고 이력</h4>
        <div class="movement-history">
          <div class="history-item">
            <div class="history-date">2024-05-28</div>
            <div class="history-content">
              <span class="movement-type in">입고</span>
              <span class="movement-qty">+50 {{ material.unit }}</span>
            </div>
            <div class="history-note">정기 발주</div>
          </div>
          <div class="history-item">
            <div class="history-date">2024-05-25</div>
            <div class="history-content">
              <span class="movement-type out">출고</span>
              <span class="movement-qty">-30 {{ material.unit }}</span>
            </div>
            <div class="history-note">생산 소모</div>
          </div>
          <div class="history-item">
            <div class="history-date">2024-05-22</div>
            <div class="history-content">
              <span class="movement-type in">입고</span>
              <span class="movement-qty">+100 {{ material.unit }}</span>
            </div>
            <div class="history-note">긴급 발주</div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">재고 분석</h4>
        <div class="analysis-grid">
          <div class="analysis-item">
            <div class="analysis-label">재고 수준</div>
            <div class="analysis-value" :class="getStockLevelClass()">
              {{ getStockLevel() }}
            </div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">소모 예측</div>
            <div class="analysis-value">{{ getPredictedConsumption() }}{{ material.unit }}/주</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">발주 필요일</div>
            <div class="analysis-value">{{ getOrderRecommendation() }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">권장 발주량</div>
            <div class="analysis-value">{{ getRecommendedOrderQty() }}{{ material.unit }}</div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <button 
        v-if="material"
        type="button" 
        class="btn btn-success"
        @click="$emit('stockIn', material)"
      >
        입고
      </button>
      <button 
        v-if="material"
        type="button" 
        class="btn btn-warning"
        @click="$emit('stockOut', material)"
      >
        출고
      </button>
      <button 
        v-if="material"
        type="button" 
        class="btn btn-secondary"
        @click="$emit('edit', material)"
      >
        수정
      </button>
      <button 
        type="button" 
        class="btn btn-primary"
        @click="$emit('update:modelValue', false)"
      >
        닫기
      </button>
    </template>
  </Modal>
</template>

<script setup lang="ts">
import Modal from '@/components/common/Modal.vue'
import Badge from '@/components/common/Badge.vue'
import type { Material } from '@/types'

interface Props {
  modelValue: boolean
  material?: Material | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'edit', material: Material): void
  (e: 'stockIn', material: Material): void
  (e: 'stockOut', material: Material): void
}

defineProps<Props>()
defineEmits<Emits>()

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

const getStockTurnover = () => {
  return (Math.random() * 5 + 2).toFixed(1)
}

const getStockLevel = () => {
  const levels = ['매우 부족', '부족', '적정', '과다']
  return levels[Math.floor(Math.random() * levels.length)]
}

const getStockLevelClass = () => {
  const level = getStockLevel()
  if (level === '매우 부족' || level === '부족') return 'warning'
  if (level === '과다') return 'info'
  return 'success'
}

const getPredictedConsumption = () => {
  return Math.floor(Math.random() * 50 + 20)
}

const getOrderRecommendation = () => {
  const days = Math.floor(Math.random() * 14 + 1)
  return `${days}일 후`
}

const getRecommendedOrderQty = () => {
  return Math.floor(Math.random() * 200 + 100)
}
</script>

<style scoped>
.detail-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.detail-value {
  font-size: 0.875rem;
  color: var(--text-primary);
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

.detail-section {
  margin-top: 1rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-color);
}

.movement-history {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.history-item {
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
  border-left: 3px solid var(--primary-color);
}

.history-date {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 0.25rem;
}

.history-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.movement-type {
  font-size: 0.75rem;
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
}

.movement-type.in {
  background-color: #dcfce7;
  color: #166534;
}

.movement-type.out {
  background-color: #fee2e2;
  color: #991b1b;
}

.movement-qty {
  font-weight: 600;
  color: var(--text-primary);
}

.history-note {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.analysis-item {
  text-align: center;
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
}

.analysis-label {
  font-size: 0.75rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.analysis-value {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.analysis-value.success {
  color: var(--success-color);
}

.analysis-value.warning {
  color: var(--warning-color);
}

.analysis-value.info {
  color: var(--primary-color);
}

@media (max-width: 768px) {
  .detail-grid,
  .analysis-grid {
    grid-template-columns: 1fr;
  }
}
</style>