<template>
  <Modal 
    :model-value="modelValue" 
    :title="isEdit ? '자재 수정' : '자재 등록'"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="handleClose"
  >
    <form @submit.prevent="handleSubmit">
      <div class="form-grid">
        <div class="form-group">
          <label for="code" class="form-label">자재코드 *</label>
          <input
            id="code"
            v-model="formData.code"
            type="text"
            class="input"
            placeholder="예: MT001"
            required
            :disabled="isEdit"
          >
          <div v-if="isEdit" class="form-hint">
            자재코드는 수정할 수 없습니다.
          </div>
        </div>

        <div class="form-group">
          <label for="name" class="form-label">자재명 *</label>
          <input
            id="name"
            v-model="formData.name"
            type="text"
            class="input"
            placeholder="예: ABS 수지"
            required
          >
        </div>

        <div class="form-group">
          <label for="category" class="form-label">카테고리 *</label>
          <select
            id="category"
            v-model="formData.category"
            class="select"
            required
          >
            <option value="">카테고리 선택</option>
            <option value="원료">원료</option>
            <option value="포장재">포장재</option>
            <option value="부자재">부자재</option>
            <option value="소모품">소모품</option>
          </select>
        </div>

        <div class="form-group">
          <label for="unit" class="form-label">단위 *</label>
          <select
            id="unit"
            v-model="formData.unit"
            class="select"
            required
          >
            <option value="">단위 선택</option>
            <option value="kg">kg</option>
            <option value="g">g</option>
            <option value="개">개</option>
            <option value="box">box</option>
            <option value="roll">roll</option>
            <option value="L">L</option>
            <option value="m">m</option>
          </select>
        </div>

        <div class="form-group">
          <label for="currentStock" class="form-label">현재 수량 *</label>
          <input
            id="currentStock"
            v-model.number="formData.currentStock"
            type="number"
            class="input"
            placeholder="0"
            min="0"
            required
          >
        </div>

        <div class="form-group">
          <label for="safetyStock" class="form-label">안전 재고 *</label>
          <input
            id="safetyStock"
            v-model.number="formData.safetyStock"
            type="number"
            class="input"
            placeholder="0"
            min="0"
            required
          >
        </div>

        <div class="form-group">
          <label for="unitPrice" class="form-label">단가</label>
          <input
            id="unitPrice"
            v-model.number="formData.unitPrice"
            type="number"
            class="input"
            placeholder="0"
            min="0"
            step="0.01"
          >
        </div>

        <div class="form-group">
          <label for="supplier" class="form-label">공급업체</label>
          <input
            id="supplier"
            v-model="formData.supplier"
            type="text"
            class="input"
            placeholder="예: (주)ABC공급"
          >
        </div>
      </div>

      <div class="form-group">
        <label for="description" class="form-label">설명</label>
        <textarea
          id="description"
          v-model="formData.description"
          class="textarea"
          rows="3"
          placeholder="자재에 대한 추가 설명을 입력하세요"
        ></textarea>
      </div>

      <div class="form-group">
        <label for="storageLocation" class="form-label">보관 위치</label>
        <input
          id="storageLocation"
          v-model="formData.storageLocation"
          type="text"
          class="input"
          placeholder="예: A창고 2구역"
        >
      </div>
    </form>

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
        class="btn btn-primary"
        @click="handleSubmit"
        :disabled="!isFormValid"
      >
        {{ isEdit ? '수정' : '등록' }}
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
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', data: Partial<Material>): void
  (e: 'close'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const isEdit = computed(() => !!props.material)

interface FormData {
  code: string
  name: string
  category: string
  unit: string
  currentStock: number
  safetyStock: number
  unitPrice?: number
  supplier?: string
  description?: string
  storageLocation?: string
}

const initialFormData: FormData = {
  code: '',
  name: '',
  category: '',
  unit: '',
  currentStock: 0,
  safetyStock: 0,
  unitPrice: 0,
  supplier: '',
  description: '',
  storageLocation: ''
}

const formData = ref<FormData>({ ...initialFormData })

const isFormValid = computed(() => {
  return !!(
    formData.value.code &&
    formData.value.name &&
    formData.value.category &&
    formData.value.unit &&
    formData.value.currentStock >= 0 &&
    formData.value.safetyStock >= 0
  )
})

const resetForm = () => {
  formData.value = { ...initialFormData }
}

const loadMaterialData = () => {
  if (props.material) {
    formData.value = {
      code: props.material.code,
      name: props.material.name,
      category: props.material.category,
      unit: props.material.unit,
      currentStock: props.material.currentStock,
      safetyStock: props.material.safetyStock,
      unitPrice: 0,
      supplier: '',
      description: '',
      storageLocation: ''
    }
  } else {
    resetForm()
  }
}

const getStatus = (currentStock: number, safetyStock: number): Material['status'] => {
  if (currentStock === 0) return 'critical'
  if (currentStock <= safetyStock * 0.5) return 'critical'
  if (currentStock <= safetyStock) return 'low'
  return 'normal'
}

const handleSubmit = () => {
  if (!isFormValid.value) return

  const submitData: Partial<Material> = {
    code: formData.value.code,
    name: formData.value.name,
    category: formData.value.category,
    unit: formData.value.unit,
    currentStock: formData.value.currentStock,
    safetyStock: formData.value.safetyStock,
    status: getStatus(formData.value.currentStock, formData.value.safetyStock)
  }

  if (isEdit.value && props.material) {
    submitData.id = props.material.id
    submitData.lastMovementDate = props.material.lastMovementDate
  } else {
    submitData.id = Date.now().toString()
    submitData.lastMovementDate = new Date().toISOString().split('T')[0]
  }

  emit('submit', submitData)
  
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
    loadMaterialData()
  }
})

watch(() => props.material, () => {
  if (props.modelValue) {
    loadMaterialData()
  }
})
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group:last-child {
  grid-column: 1 / -1;
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

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>