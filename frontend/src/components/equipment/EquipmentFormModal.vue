<template>
  <Modal 
    :model-value="modelValue" 
    :title="isEdit ? '설비 수정' : '설비 등록'"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="handleClose"
  >
    <form @submit.prevent="handleSubmit">
      <div class="form-grid">
        <div class="form-group">
          <label for="code" class="form-label">설비코드 *</label>
          <input
            id="code"
            v-model="formData.code"
            type="text"
            class="input"
            placeholder="예: EQ001"
            required
            :disabled="isEdit"
          >
          <div v-if="isEdit" class="form-hint">
            설비코드는 수정할 수 없습니다.
          </div>
        </div>

        <div class="form-group">
          <label for="name" class="form-label">설비명 *</label>
          <input
            id="name"
            v-model="formData.name"
            type="text"
            class="input"
            placeholder="예: 사출성형기 A"
            required
          >
        </div>

        <div class="form-group">
          <label for="location" class="form-label">위치 *</label>
          <input
            id="location"
            v-model="formData.location"
            type="text"
            class="input"
            placeholder="예: 1공장 A라인"
            required
          >
        </div>

        <div class="form-group">
          <label for="status" class="form-label">상태 *</label>
          <select
            id="status"
            v-model="formData.status"
            class="select"
            required
          >
            <option value="">상태 선택</option>
            <option value="active">가동</option>
            <option value="stopped">정지</option>
            <option value="maintenance">점검</option>
            <option value="error">이상</option>
          </select>
        </div>

        <div class="form-group">
          <label for="manager" class="form-label">담당자 *</label>
          <input
            id="manager"
            v-model="formData.manager"
            type="text"
            class="input"
            placeholder="예: 김철수"
            required
          >
        </div>

        <div class="form-group">
          <label for="lastCheckDate" class="form-label">최근 점검일</label>
          <input
            id="lastCheckDate"
            v-model="formData.lastCheckDate"
            type="date"
            class="input"
          >
        </div>
      </div>

      <div v-if="!isEdit" class="form-group">
        <label for="description" class="form-label">설비 설명</label>
        <textarea
          id="description"
          v-model="formData.description"
          class="textarea"
          rows="3"
          placeholder="설비에 대한 추가 설명을 입력하세요"
        ></textarea>
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
import type { Equipment } from '@/types'

interface Props {
  modelValue: boolean
  equipment?: Equipment | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', data: Partial<Equipment>): void
  (e: 'close'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const isEdit = computed(() => !!props.equipment)

interface FormData {
  code: string
  name: string
  location: string
  status: Equipment['status'] | ''
  manager: string
  lastCheckDate: string
  description?: string
}

const initialFormData: FormData = {
  code: '',
  name: '',
  location: '',
  status: '',
  manager: '',
  lastCheckDate: '',
  description: ''
}

const formData = ref<FormData>({ ...initialFormData })

const isFormValid = computed(() => {
  return !!(
    formData.value.code &&
    formData.value.name &&
    formData.value.location &&
    formData.value.status &&
    formData.value.manager
  )
})

const resetForm = () => {
  formData.value = { ...initialFormData }
}

const loadEquipmentData = () => {
  if (props.equipment) {
    formData.value = {
      code: props.equipment.code,
      name: props.equipment.name,
      location: props.equipment.location,
      status: props.equipment.status,
      manager: props.equipment.manager,
      lastCheckDate: props.equipment.lastCheckDate,
      description: ''
    }
  } else {
    resetForm()
  }
}

const handleSubmit = () => {
  if (!isFormValid.value) return

  const submitData: Partial<Equipment> = {
    code: formData.value.code,
    name: formData.value.name,
    location: formData.value.location,
    status: formData.value.status as Equipment['status'],
    manager: formData.value.manager,
    lastCheckDate: formData.value.lastCheckDate,
  }

  if (isEdit.value && props.equipment) {
    submitData.id = props.equipment.id
    submitData.registeredDate = props.equipment.registeredDate
  } else {
    submitData.id = Date.now().toString()
    submitData.registeredDate = new Date().toISOString().split('T')[0]
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
    loadEquipmentData()
  }
})

watch(() => props.equipment, () => {
  if (props.modelValue) {
    loadEquipmentData()
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