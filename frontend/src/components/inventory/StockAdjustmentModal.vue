<template>
  <Modal 
    :model-value="modelValue" 
    :title="type === 'in' ? '재고 입고' : '재고 출고'"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="handleClose"
  >
    <div v-if="material" class="adjustment-container">
      <div class="material-info">
        <h4 class="material-title">{{ material.name }} ({{ material.code }})</h4>
        <div class="current-stock">
          현재 재고: <span class="stock-amount">{{ material.currentStock }} {{ material.unit }}</span>
        </div>
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="quantity" class="form-label">
            {{ type === 'in' ? '입고' : '출고' }} 수량 *
          </label>
          <input
            id="quantity"
            v-model.number="formData.quantity"
            type="number"
            class="input"
            :placeholder="`${type === 'in' ? '입고' : '출고'}할 수량을 입력하세요`"
            min="1"
            :max="type === 'out' ? material.currentStock : undefined"
            required
          >
          <div v-if="type === 'out'" class="form-hint">
            최대 출고 가능 수량: {{ material.currentStock }} {{ material.unit }}
          </div>
        </div>

        <div class="form-group">
          <label for="reason" class="form-label">사유 *</label>
          <select
            id="reason"
            v-model="formData.reason"
            class="select"
            required
          >
            <option value="">사유 선택</option>
            <template v-if="type === 'in'">
              <option value="purchase">구매입고</option>
              <option value="return">반품입고</option>
              <option value="transfer">이고입고</option>
              <option value="adjustment">재고조정</option>
              <option value="other">기타</option>
            </template>
            <template v-else>
              <option value="production">생산사용</option>
              <option value="sale">판매출고</option>
              <option value="transfer">이고출고</option>
              <option value="disposal">폐기</option>
              <option value="adjustment">재고조정</option>
              <option value="other">기타</option>
            </template>
          </select>
        </div>

        <div v-if="type === 'in'" class="form-group">
          <label for="supplier" class="form-label">공급업체</label>
          <input
            id="supplier"
            v-model="formData.supplier"
            type="text"
            class="input"
            placeholder="공급업체명을 입력하세요"
          >
        </div>

        <div v-if="type === 'in'" class="form-group">
          <label for="unitPrice" class="form-label">단가</label>
          <input
            id="unitPrice"
            v-model.number="formData.unitPrice"
            type="number"
            class="input"
            placeholder="단가를 입력하세요"
            min="0"
            step="0.01"
          >
        </div>

        <div class="form-group">
          <label for="lotNumber" class="form-label">LOT 번호</label>
          <input
            id="lotNumber"
            v-model="formData.lotNumber"
            type="text"
            class="input"
            placeholder="LOT 번호를 입력하세요"
          >
        </div>

        <div class="form-group">
          <label for="notes" class="form-label">비고</label>
          <textarea
            id="notes"
            v-model="formData.notes"
            class="textarea"
            rows="3"
            placeholder="추가 설명이나 특이사항을 입력하세요"
          ></textarea>
        </div>
      </form>

      <div v-if="formData.quantity > 0" class="preview-section">
        <h4 class="preview-title">변경 후 예상 재고</h4>
        <div class="preview-stock">
          <div class="preview-item">
            <span class="preview-label">현재:</span>
            <span class="preview-value">{{ material.currentStock }} {{ material.unit }}</span>
          </div>
          <div class="preview-arrow">→</div>
          <div class="preview-item">
            <span class="preview-label">변경 후:</span>
            <span class="preview-value" :class="getNewStockClass()">
              {{ getNewStockAmount() }} {{ material.unit }}
            </span>
          </div>
        </div>
        <div v-if="getNewStockAmount() <= material.safetyStock" class="warning-message">
          ⚠️ 안전재고({{ material.safetyStock }}{{ material.unit }}) 이하로 떨어집니다.
        </div>
      </div>
    </div>

    <template #footer>
      <button 
        type="button" 
        class="btn btn-secondary"
        @click="handleClose"
      >
        취소
      </button>
      <button 
        type="button" 
        class="btn"
        :class="type === 'in' ? 'btn-success' : 'btn-warning'"
        @click="handleSubmit"
        :disabled="!isFormValid"
      >
        {{ type === 'in' ? '입고' : '출고' }} 처리
      </button>
    </template>
  </Modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import Modal from '@/components/common/Modal.vue'
import type { Material } from '@/types'

interface Props {
  modelValue: boolean
  material?: Material | null
  type: 'in' | 'out'
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', data: {
    material: Material
    type: 'in' | 'out'
    quantity: number
    reason: string
    supplier?: string
    unitPrice?: number
    lotNumber?: string
    notes?: string
  }): void
  (e: 'close'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

interface FormData {
  quantity: number
  reason: string
  supplier?: string
  unitPrice?: number
  lotNumber?: string
  notes?: string
}

const initialFormData: FormData = {
  quantity: 0,
  reason: '',
  supplier: '',
  unitPrice: 0,
  lotNumber: '',
  notes: ''
}

const formData = ref<FormData>({ ...initialFormData })

const isFormValid = computed(() => {
  if (!props.material) return false
  
  const hasValidQuantity = formData.value.quantity > 0
  const hasValidReason = !!formData.value.reason
  const hasEnoughStock = props.type === 'in' || formData.value.quantity <= props.material.currentStock
  
  return hasValidQuantity && hasValidReason && hasEnoughStock
})

const getNewStockAmount = () => {
  if (!props.material) return 0
  
  if (props.type === 'in') {
    return props.material.currentStock + formData.value.quantity
  } else {
    return props.material.currentStock - formData.value.quantity
  }
}

const getNewStockClass = () => {
  const newAmount = getNewStockAmount()
  if (!props.material) return ''
  
  if (newAmount <= props.material.safetyStock * 0.5) return 'stock-critical'
  if (newAmount <= props.material.safetyStock) return 'stock-low'
  return 'stock-normal'
}

const resetForm = () => {
  formData.value = { ...initialFormData }
}

const handleSubmit = () => {
  if (!isFormValid.value || !props.material) return

  emit('submit', {
    material: props.material,
    type: props.type,
    quantity: formData.value.quantity,
    reason: formData.value.reason,
    supplier: formData.value.supplier,
    unitPrice: formData.value.unitPrice,
    lotNumber: formData.value.lotNumber,
    notes: formData.value.notes
  })
  
  // 모달 닫기
  emit('update:modelValue', false)
  emit('close')
  resetForm()
}

const handleClose = () => {
  emit('update:modelValue', false)
  emit('close')
  resetForm()
}

watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    resetForm()
  }
})
</script>

<style scoped>
.adjustment-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.material-info {
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 0.375rem;
  border-left: 3px solid var(--primary-color);
}

.material-title {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.current-stock {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.stock-amount {
  font-weight: 600;
  color: var(--text-primary);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-primary);
}

.form-hint {
  font-size: 0.75rem;
  color: var(--text-secondary);
  font-style: italic;
}

.textarea {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 0.375rem;
  font-size: 0.875rem;
  background-color: var(--bg-primary);
  resize: vertical;
  font-family: inherit;
}

.textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgb(59 130 246 / 0.1);
}

.preview-section {
  padding: 1rem;
  background-color: var(--bg-tertiary);
  border-radius: 0.375rem;
  border: 1px dashed var(--border-color);
}

.preview-title {
  margin: 0 0 0.75rem 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

.preview-stock {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.preview-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.preview-label {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.preview-value {
  font-size: 0.875rem;
  font-weight: 600;
}

.preview-arrow {
  font-size: 1.25rem;
  color: var(--text-secondary);
}

.warning-message {
  font-size: 0.75rem;
  color: var(--warning-color);
  font-weight: 500;
  padding: 0.5rem;
  background-color: #fef3c7;
  border-radius: 0.25rem;
  border-left: 3px solid var(--warning-color);
}

.stock-normal {
  color: var(--success-color);
}

.stock-low {
  color: var(--warning-color);
}

.stock-critical {
  color: var(--error-color);
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