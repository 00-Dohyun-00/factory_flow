<template>
  <Modal 
    :model-value="modelValue" 
    title="설비 상세 정보"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div v-if="equipment" class="detail-container">
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">설비코드</div>
          <div class="detail-value">{{ equipment.code }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">설비명</div>
          <div class="detail-value">{{ equipment.name }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">위치</div>
          <div class="detail-value">{{ equipment.location }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">상태</div>
          <div class="detail-value">
            <Badge :variant="getStatusVariant(equipment.status)">
              {{ getStatusText(equipment.status) }}
            </Badge>
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-label">담당자</div>
          <div class="detail-value">{{ equipment.manager }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">최근 점검일</div>
          <div class="detail-value">{{ formatDate(equipment.lastCheckDate) }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">등록일</div>
          <div class="detail-value">{{ formatDate(equipment.registeredDate) }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">가동 시간</div>
          <div class="detail-value">{{ getOperatingHours() }}시간</div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">최근 점검 이력</h4>
        <div class="inspection-history">
          <div class="history-item">
            <div class="history-date">2024-05-25</div>
            <div class="history-content">정기점검 완료 - 이상 없음</div>
            <div class="history-inspector">점검자: 김기사</div>
          </div>
          <div class="history-item">
            <div class="history-date">2024-05-20</div>
            <div class="history-content">오일 교체 및 청소</div>
            <div class="history-inspector">점검자: 이기사</div>
          </div>
          <div class="history-item">
            <div class="history-date">2024-05-15</div>
            <div class="history-content">벨트 장력 조정</div>
            <div class="history-inspector">점검자: 박기사</div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">성능 지표</h4>
        <div class="performance-grid">
          <div class="performance-item">
            <div class="performance-label">가동률</div>
            <div class="performance-value success">95.2%</div>
          </div>
          <div class="performance-item">
            <div class="performance-label">평균 처리량</div>
            <div class="performance-value">850개/시간</div>
          </div>
          <div class="performance-item">
            <div class="performance-label">고장률</div>
            <div class="performance-value warning">2.1%</div>
          </div>
          <div class="performance-item">
            <div class="performance-label">효율성</div>
            <div class="performance-value success">92.8%</div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <button 
        v-if="equipment"
        type="button" 
        class="btn btn-secondary"
        @click="$emit('edit', equipment)"
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
import type { Equipment } from '@/types'

interface Props {
  modelValue: boolean
  equipment?: Equipment | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'edit', equipment: Equipment): void
}

defineProps<Props>()
defineEmits<Emits>()

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

const getOperatingHours = () => {
  return Math.floor(Math.random() * 1000) + 500
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

.inspection-history {
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
  font-size: 0.875rem;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.history-inspector {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.performance-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.performance-item {
  text-align: center;
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
}

.performance-label {
  font-size: 0.75rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.performance-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
}

.performance-value.success {
  color: var(--success-color);
}

.performance-value.warning {
  color: var(--warning-color);
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .performance-grid {
    grid-template-columns: 1fr;
  }
}
</style>