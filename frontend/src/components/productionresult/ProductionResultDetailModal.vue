<template>
  <Modal 
    :model-value="modelValue" 
    title="생산실적 상세 정보"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div v-if="productionResult" class="detail-container">
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">실적번호</div>
          <div class="detail-value">{{ productionResult.resultNumber }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">작업지시번호</div>
          <div class="detail-value">{{ productionResult.orderNumber }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">제품명</div>
          <div class="detail-value">{{ productionResult.productName }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">생산수량</div>
          <div class="detail-value">{{ productionResult.productionQuantity.toLocaleString() }}개</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">불량수량</div>
          <div class="detail-value defect-quantity">{{ productionResult.defectQuantity.toLocaleString() }}개</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">수율</div>
          <div class="detail-value" :class="getYieldClass(productionResult.yieldRate)">{{ productionResult.yieldRate }}%</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">사용설비</div>
          <div class="detail-value">{{ getEquipmentName() }}</div>
        </div>

        <div class="detail-item">
          <div class="detail-label">작업일</div>
          <div class="detail-value">{{ formatDate(productionResult.workDate) }}</div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">품질 분석</h4>
        <div class="quality-analysis">
          <div class="analysis-grid">
            <div class="analysis-item">
              <div class="analysis-label">목표 수율</div>
              <div class="analysis-value">98.0%</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">실제 수율</div>
              <div class="analysis-value" :class="getYieldClass(productionResult.yieldRate)">{{ productionResult.yieldRate }}%</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">수율 차이</div>
              <div class="analysis-value" :class="getYieldDiffClass()">{{ getYieldDiff() }}%</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">품질 등급</div>
              <div class="analysis-value">{{ getQualityGrade() }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">불량 현황</h4>
        <div class="defect-breakdown">
          <div class="defect-item">
            <div class="defect-type">치수 불량</div>
            <div class="defect-count">{{ Math.floor(productionResult.defectQuantity * 0.4) }}개 (40%)</div>
            <div class="defect-bar">
              <div class="defect-fill" style="width: 40%"></div>
            </div>
          </div>
          <div class="defect-item">
            <div class="defect-type">표면 결함</div>
            <div class="defect-count">{{ Math.floor(productionResult.defectQuantity * 0.3) }}개 (30%)</div>
            <div class="defect-bar">
              <div class="defect-fill" style="width: 30%"></div>
            </div>
          </div>
          <div class="defect-item">
            <div class="defect-type">기타</div>
            <div class="defect-count">{{ Math.floor(productionResult.defectQuantity * 0.3) }}개 (30%)</div>
            <div class="defect-bar">
              <div class="defect-fill" style="width: 30%"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-title">작업 정보</h4>
        <div class="work-info">
          <div class="info-item">
            <div class="info-label">작업 시작시간</div>
            <div class="info-value">{{ getWorkStartTime() }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">작업 종료시간</div>
            <div class="info-value">{{ getWorkEndTime() }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">작업 소요시간</div>
            <div class="info-value">{{ getWorkDuration() }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">담당 작업자</div>
            <div class="info-value">{{ getWorker() }}</div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <button 
        v-if="productionResult"
        type="button" 
        class="btn btn-secondary"
        @click="$emit('edit', productionResult)"
      >
        수정
      </button>
      <button 
        type="button" 
        class="btn btn-secondary"
        @click="printResult"
      >
        출력
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
import type { ProductionResult } from '@/types'

interface Props {
  modelValue: boolean
  productionResult?: ProductionResult | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'edit', productionResult: ProductionResult): void
}

const props = defineProps<Props>()
defineEmits<Emits>()

const getYieldClass = (yieldValue: number) => {
  if (yieldValue >= 98) return 'yield-excellent'
  if (yieldValue >= 95) return 'yield-good'
  if (yieldValue >= 90) return 'yield-warning'
  return 'yield-poor'
}

const getYieldDiff = () => {
  if (!props.productionResult) return '0.0'
  return (props.productionResult.yieldRate - 98.0).toFixed(1)
}

const getYieldDiffClass = () => {
  if (!props.productionResult) return ''
  const diff = props.productionResult.yieldRate - 98.0
  return diff >= 0 ? 'positive' : 'negative'
}

const getQualityGrade = () => {
  if (!props.productionResult) return 'C'
  const yieldRate = props.productionResult.yieldRate
  if (yieldRate >= 99) return 'A+'
  if (yieldRate >= 98) return 'A'
  if (yieldRate >= 95) return 'B'
  if (yieldRate >= 90) return 'C'
  return 'D'
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
  return equipmentMap[props.productionResult?.equipment || ''] || props.productionResult?.equipment
}

const getWorkStartTime = () => {
  return '08:00'
}

const getWorkEndTime = () => {
  return '17:00'
}

const getWorkDuration = () => {
  return '8시간'
}

const getWorker = () => {
  const workers = ['김작업', '이생산', '박제조', '최공장']
  return workers[Math.floor(Math.random() * workers.length)]
}

const printResult = () => {
  console.log('실적 출력:', props.productionResult)
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

.defect-quantity {
  color: var(--error-color);
  font-weight: 500;
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
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
}

.yield-excellent {
  color: var(--success-color);
}

.yield-good {
  color: #22c55e;
}

.yield-warning {
  color: var(--warning-color);
}

.yield-poor {
  color: var(--error-color);
}

.positive {
  color: var(--success-color);
}

.negative {
  color: var(--error-color);
}

.defect-breakdown {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.defect-item {
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
  border-left: 3px solid var(--error-color);
}

.defect-type {
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.defect-count {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.defect-bar {
  height: 6px;
  background-color: var(--bg-tertiary);
  border-radius: 3px;
  overflow: hidden;
}

.defect-fill {
  height: 100%;
  background-color: var(--error-color);
  transition: width 0.3s ease;
}

.work-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
}

.info-label {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.info-value {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .detail-grid,
  .analysis-grid,
  .work-info {
    grid-template-columns: 1fr;
  }
}
</style>