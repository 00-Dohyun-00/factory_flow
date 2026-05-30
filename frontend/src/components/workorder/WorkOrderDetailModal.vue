<template>
  <Modal 
    :model-value="modelValue" 
    title="작업지시 상세 정보"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div v-if="workOrder" class="detail-container">
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">작업지시번호</div>
          <div class="detail-value">{{ workOrder.orderNumber }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">제품명</div>
          <div class="detail-value">{{ workOrder.productName }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">목표수량</div>
          <div class="detail-value">{{ workOrder.targetQuantity.toLocaleString() }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">완료수량</div>
          <div class="detail-value">{{ workOrder.completedQuantity.toLocaleString() }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">진행률</div>
          <div class="detail-value">
            <div class="progress-container">
              <div class="progress-bar">
                <div 
                  class="progress-fill" 
                  :style="{ width: getProgressPercentage() + '%' }"
                ></div>
              </div>
              <span class="progress-text">{{ getProgressPercentage() }}%</span>
            </div>
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-label">담당설비</div>
          <div class="detail-value">{{ getEquipmentName() }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">상태</div>
          <div class="detail-value">
            <Badge :variant="getStatusVariant(workOrder.status)">
              {{ getStatusText(workOrder.status) }}
            </Badge>
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-label">시작예정일</div>
          <div class="detail-value">{{ formatDate(workOrder.startDate) }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">납기일</div>
          <div class="detail-value" :class="getDueDateClass()">
            {{ formatDate(workOrder.dueDate) }}
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">작업 진행 상황</h4>
        <div class="progress-timeline">
          <div class="timeline-item" :class="{ completed: workOrder.status !== 'waiting' }">
            <div class="timeline-icon">📋</div>
            <div class="timeline-content">
              <div class="timeline-title">작업 지시 발행</div>
              <div class="timeline-date">{{ formatDate(workOrder.startDate) }}</div>
            </div>
          </div>
          <div class="timeline-item" :class="{ completed: workOrder.status === 'in_progress' || workOrder.status === 'completed' }">
            <div class="timeline-icon">⚡</div>
            <div class="timeline-content">
              <div class="timeline-title">작업 시작</div>
              <div class="timeline-date">{{ getStartDate() }}</div>
            </div>
          </div>
          <div class="timeline-item" :class="{ completed: workOrder.status === 'completed' }">
            <div class="timeline-icon">✅</div>
            <div class="timeline-content">
              <div class="timeline-title">작업 완료</div>
              <div class="timeline-date">{{ getCompletionDate() }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">필요 자재</h4>
        <div class="material-list">
          <div class="material-item">
            <div class="material-name">ABS 수지</div>
            <div class="material-quantity">필요: 100kg / 사용: {{ Math.floor(getProgressPercentage() * 1) }}kg</div>
            <div class="material-status available">재고 충분</div>
          </div>
          <div class="material-item">
            <div class="material-name">포장박스</div>
            <div class="material-quantity">필요: 50개 / 사용: {{ Math.floor(getProgressPercentage() * 0.5) }}개</div>
            <div class="material-status low">재고 부족</div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">품질 정보</h4>
        <div class="quality-grid">
          <div class="quality-item">
            <div class="quality-label">목표 품질</div>
            <div class="quality-value">98% 이상</div>
          </div>
          <div class="quality-item">
            <div class="quality-label">현재 수율</div>
            <div class="quality-value success">{{ getCurrentYield() }}%</div>
          </div>
          <div class="quality-item">
            <div class="quality-label">불량률</div>
            <div class="quality-value">{{ (100 - parseFloat(getCurrentYield())).toFixed(1) }}%</div>
          </div>
          <div class="quality-item">
            <div class="quality-label">검사상태</div>
            <div class="quality-value">정상</div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <button 
        v-if="workOrder?.status === 'waiting'"
        type="button" 
        class="btn btn-success"
        @click="$emit('start', workOrder)"
      >
        작업 시작
      </button>
      <button 
        v-if="workOrder?.status === 'in_progress'"
        type="button" 
        class="btn btn-warning"
        @click="$emit('pause', workOrder)"
      >
        일시 정지
      </button>
      <button 
        v-if="workOrder?.status === 'in_progress'"
        type="button" 
        class="btn btn-success"
        @click="$emit('complete', workOrder)"
      >
        작업 완료
      </button>
      <button 
        v-if="workOrder"
        type="button" 
        class="btn btn-secondary"
        @click="$emit('edit', workOrder)"
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
import type { WorkOrder } from '@/types'

interface Props {
  modelValue: boolean
  workOrder?: WorkOrder | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'edit', workOrder: WorkOrder): void
  (e: 'start', workOrder: WorkOrder): void
  (e: 'pause', workOrder: WorkOrder): void
  (e: 'complete', workOrder: WorkOrder): void
}

const props = defineProps<Props>()
defineEmits<Emits>()

const getProgressPercentage = () => {
  if (!props.workOrder || props.workOrder.targetQuantity === 0) return 0
  return Math.round((props.workOrder.completedQuantity / props.workOrder.targetQuantity) * 100)
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

const getDueDateClass = () => {
  if (!props.workOrder) return ''
  
  const today = new Date()
  const due = new Date(props.workOrder.dueDate)
  const diffDays = Math.ceil((due.getTime() - today.getTime()) / (1000 * 3600 * 24))
  
  if (diffDays < 0) return 'overdue'
  if (diffDays <= 3) return 'urgent'
  return 'normal'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const getEquipmentName = () => {
  const equipmentMap: Record<string, string> = {
    'EQ001': 'EQ001 - 사출성형기 A',
    'EQ002': 'EQ002 - 포장기계 B',
    'EQ003': 'EQ003 - 절단기 C',
    'EQ004': 'EQ004 - 검사장비 D'
  }
  return equipmentMap[props.workOrder?.equipment || ''] || props.workOrder?.equipment
}

const getStartDate = () => {
  if (props.workOrder?.status === 'waiting') return '-'
  return formatDate(props.workOrder?.startDate || '')
}

const getCompletionDate = () => {
  if (props.workOrder?.status !== 'completed') return '-'
  return formatDate(new Date().toISOString().split('T')[0])
}

const getCurrentYield = () => {
  return (Math.random() * 5 + 95).toFixed(1)
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

.progress-timeline {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.timeline-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
  opacity: 0.5;
  transition: opacity 0.3s ease;
}

.timeline-item.completed {
  opacity: 1;
  border-left: 3px solid var(--success-color);
}

.timeline-icon {
  font-size: 1.5rem;
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  font-weight: 500;
  color: var(--text-primary);
}

.timeline-date {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.material-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.material-item {
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
  border-left: 3px solid var(--primary-color);
}

.material-name {
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.material-quantity {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-bottom: 0.25rem;
}

.material-status {
  font-size: 0.75rem;
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  display: inline-block;
}

.material-status.available {
  background-color: #dcfce7;
  color: #166534;
}

.material-status.low {
  background-color: #fef3c7;
  color: #92400e;
}

.quality-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.quality-item {
  text-align: center;
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
}

.quality-label {
  font-size: 0.75rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.quality-value {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.quality-value.success {
  color: var(--success-color);
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

@media (max-width: 768px) {
  .detail-grid,
  .quality-grid {
    grid-template-columns: 1fr;
  }
}
</style>