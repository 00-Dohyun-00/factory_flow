<template>
  <Modal 
    :model-value="modelValue" 
    :title="isEditing ? '생산실적 수정' : '생산실적 등록'"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <form @submit.prevent="handleSubmit" class="form-container">
      <div class="form-grid">
        <div class="form-group">
          <label for="resultNumber" class="form-label">실적번호</label>
          <input
            id="resultNumber"
            v-model="formData.resultNumber"
            type="text"
            class="input"
            placeholder="PR2024XXX"
            :readonly="isEditing"
            required
          >
        </div>

        <div class="form-group">
          <label for="orderNumber" class="form-label">작업지시번호</label>
          <select 
            id="orderNumber"
            v-model="formData.orderNumber" 
            class="select"
            required
            @change="handleOrderChange"
          >
            <option value="">작업지시 선택</option>
            <option value="WO2024001">WO2024001 - 플라스틱 부품 A</option>
            <option value="WO2024002">WO2024002 - 포장박스 세트</option>
            <option value="WO2024003">WO2024003 - 금속 부품 B</option>
            <option value="WO2024004">WO2024004 - 검사완료 제품</option>
          </select>
        </div>

        <div class="form-group">
          <label for="productName" class="form-label">제품명</label>
          <input
            id="productName"
            v-model="formData.productName"
            type="text"
            class="input"
            readonly
          >
        </div>

        <div class="form-group">
          <label for="equipment" class="form-label">사용설비</label>
          <select 
            id="equipment"
            v-model="formData.equipment" 
            class="select"
            required
          >
            <option value="">설비 선택</option>
            <option value="EQ001">EQ001 - 사출성형기 A</option>
            <option value="EQ002">EQ002 - 포장기계 B</option>
            <option value="EQ003">EQ003 - 절단기 C</option>
            <option value="EQ004">EQ004 - 검사장비 D</option>
          </select>
        </div>

        <div class="form-group">
          <label for="productionQuantity" class="form-label">생산수량</label>
          <div class="input-with-unit">
            <input
              id="productionQuantity"
              v-model.number="formData.productionQuantity"
              type="number"
              class="input"
              placeholder="0"
              min="0"
              required
              @input="calculateYield"
            >
            <span class="input-unit">개</span>
          </div>
        </div>

        <div class="form-group">
          <label for="defectQuantity" class="form-label">불량수량</label>
          <div class="input-with-unit">
            <input
              id="defectQuantity"
              v-model.number="formData.defectQuantity"
              type="number"
              class="input"
              placeholder="0"
              min="0"
              required
              @input="calculateYield"
            >
            <span class="input-unit">개</span>
          </div>
        </div>

        <div class="form-group">
          <label for="yieldRate" class="form-label">수율</label>
          <div class="yield-display">
            <span class="yield-value" :class="getYieldClass(formData.yieldRate)">
              {{ formData.yieldRate }}%
            </span>
            <span class="yield-status">{{ getYieldStatus(formData.yieldRate) }}</span>
          </div>
        </div>

        <div class="form-group">
          <label for="workDate" class="form-label">작업일</label>
          <input
            id="workDate"
            v-model="formData.workDate"
            type="date"
            class="input"
            required
          >
        </div>
      </div>

      <div class="form-section">
        <h4 class="section-title">추가 정보</h4>
        <div class="form-row">
          <div class="form-group">
            <label for="workStartTime" class="form-label">작업 시작시간</label>
            <input
              id="workStartTime"
              v-model="formData.workStartTime"
              type="time"
              class="input"
            >
          </div>

          <div class="form-group">
            <label for="workEndTime" class="form-label">작업 종료시간</label>
            <input
              id="workEndTime"
              v-model="formData.workEndTime"
              type="time"
              class="input"
            >
          </div>
        </div>

        <div class="form-group">
          <label for="worker" class="form-label">담당 작업자</label>
          <select 
            id="worker"
            v-model="formData.worker" 
            class="select"
          >
            <option value="">작업자 선택</option>
            <option value="김작업">김작업</option>
            <option value="이생산">이생산</option>
            <option value="박제조">박제조</option>
            <option value="최공장">최공장</option>
          </select>
        </div>

        <div class="form-group">
          <label for="notes" class="form-label">비고</label>
          <textarea
            id="notes"
            v-model="formData.notes"
            class="textarea"
            rows="3"
            placeholder="특이사항이나 비고사항을 입력하세요"
          ></textarea>
        </div>
      </div>
    </form>

    <template #footer>
      <button 
        type="button" 
        class="btn btn-secondary"
        @click="$emit('update:modelValue', false)"
      >
        취소
      </button>
      <button 
        type="button" 
        class="btn btn-primary"
        @click="handleSubmit"
      >
        {{ isEditing ? '수정' : '등록' }}
      </button>
    </template>
  </Modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import Modal from '@/components/common/Modal.vue'
import type { ProductionResult } from '@/types'

interface Props {
  modelValue: boolean
  productionResult?: ProductionResult | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', formData: Partial<ProductionResult>): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

interface FormData {
  resultNumber: string
  orderNumber: string
  productName: string
  productionQuantity: number
  defectQuantity: number
  yieldRate: number
  equipment: string
  workDate: string
  workStartTime: string
  workEndTime: string
  worker: string
  notes: string
}

const formData = ref<FormData>({
  resultNumber: '',
  orderNumber: '',
  productName: '',
  productionQuantity: 0,
  defectQuantity: 0,
  yieldRate: 0,
  equipment: '',
  workDate: new Date().toISOString().split('T')[0],
  workStartTime: '08:00',
  workEndTime: '17:00',
  worker: '',
  notes: ''
})

const isEditing = computed(() => !!props.productionResult)

const orderProductMap: Record<string, string> = {
  'WO2024001': '플라스틱 부품 A',
  'WO2024002': '포장박스 세트',
  'WO2024003': '금속 부품 B',
  'WO2024004': '검사완료 제품'
}

const resetForm = () => {
  formData.value = {
    resultNumber: '',
    orderNumber: '',
    productName: '',
    productionQuantity: 0,
    defectQuantity: 0,
    yieldRate: 0,
    equipment: '',
    workDate: new Date().toISOString().split('T')[0],
    workStartTime: '08:00',
    workEndTime: '17:00',
    worker: '',
    notes: ''
  }
}

const generateResultNumber = () => {
  const today = new Date()
  const year = today.getFullYear()
  const sequence = String(Math.floor(Math.random() * 1000) + 1).padStart(3, '0')
  formData.value.resultNumber = `PR${year}${sequence}`
}

watch(() => props.productionResult, (newResult) => {
  if (newResult) {
    formData.value = {
      resultNumber: newResult.resultNumber,
      orderNumber: newResult.orderNumber,
      productName: newResult.productName,
      productionQuantity: newResult.productionQuantity,
      defectQuantity: newResult.defectQuantity,
      yieldRate: newResult.yieldRate,
      equipment: newResult.equipment,
      workDate: newResult.workDate,
      workStartTime: '08:00',
      workEndTime: '17:00',
      worker: '',
      notes: ''
    }
  } else {
    resetForm()
  }
}, { immediate: true })

watch(() => props.modelValue, (isOpen) => {
  if (isOpen && !props.productionResult) {
    resetForm()
    generateResultNumber()
  }
})

const handleOrderChange = () => {
  if (formData.value.orderNumber && orderProductMap[formData.value.orderNumber]) {
    formData.value.productName = orderProductMap[formData.value.orderNumber]
  } else {
    formData.value.productName = ''
  }
}

const calculateYield = () => {
  const total = formData.value.productionQuantity + formData.value.defectQuantity
  if (total > 0) {
    formData.value.yieldRate = parseFloat(((formData.value.productionQuantity / total) * 100).toFixed(1))
  } else {
    formData.value.yieldRate = 0
  }
}

const getYieldClass = (yieldValue: number) => {
  if (yieldValue >= 98) return 'yield-excellent'
  if (yieldValue >= 95) return 'yield-good'
  if (yieldValue >= 90) return 'yield-warning'
  return 'yield-poor'
}

const getYieldStatus = (yieldValue: number) => {
  if (yieldValue >= 98) return '우수'
  if (yieldValue >= 95) return '양호'
  if (yieldValue >= 90) return '주의'
  return '불량'
}

const handleSubmit = () => {
  emit('submit', formData.value)
  emit('update:modelValue', false)
}
</script>

<style scoped>
.form-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-primary);
}

.input-with-unit {
  display: flex;
  align-items: center;
  position: relative;
}

.input-with-unit .input {
  padding-right: 2rem;
}

.input-unit {
  position: absolute;
  right: 0.75rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
  pointer-events: none;
}

.yield-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
  border: 1px solid var(--border-color);
}

.yield-value {
  font-size: 1rem;
  font-weight: 600;
}

.yield-status {
  font-size: 0.875rem;
  color: var(--text-secondary);
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

.form-section {
  border-top: 1px solid var(--border-color);
  padding-top: 1.5rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.textarea {
  min-height: 80px;
  resize: vertical;
}

@media (max-width: 768px) {
  .form-grid,
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>