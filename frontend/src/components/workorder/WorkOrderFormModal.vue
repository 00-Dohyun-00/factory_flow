<template>
  <Modal 
    :model-value="modelValue" 
    :title="isEdit ? '작업지시 수정' : '작업지시 등록'"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="handleClose"
  >
    <form @submit.prevent="handleSubmit">
      <div class="form-grid">
        <div class="form-group">
          <label for="orderNumber" class="form-label">작업지시번호 *</label>
          <input
            id="orderNumber"
            v-model="formData.orderNumber"
            type="text"
            class="input"
            placeholder="예: WO2024001"
            required
            :disabled="isEdit"
          >
          <div v-if="isEdit" class="form-hint">
            작업지시번호는 수정할 수 없습니다.
          </div>
        </div>

        <div class="form-group">
          <label for="productName" class="form-label">제품명 *</label>
          <input
            id="productName"
            v-model="formData.productName"
            type="text"
            class="input"
            placeholder="예: 플라스틱 부품 A"
            required
          >
        </div>

        <div class="form-group">
          <label for="targetQuantity" class="form-label">목표수량 *</label>
          <input
            id="targetQuantity"
            v-model.number="formData.targetQuantity"
            type="number"
            class="input"
            placeholder="0"
            min="1"
            required
          >
        </div>

        <div class="form-group">
          <label for="equipment" class="form-label">담당설비 *</label>
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
          <label for="startDate" class="form-label">시작예정일 *</label>
          <input
            id="startDate"
            v-model="formData.startDate"
            type="date"
            class="input"
            required
          >
        </div>

        <div class="form-group">
          <label for="dueDate" class="form-label">납기일 *</label>
          <input
            id="dueDate"
            v-model="formData.dueDate"
            type="date"
            class="input"
            :min="formData.startDate"
            required
          >
        </div>

        <div v-if="isEdit" class="form-group">
          <label for="completedQuantity" class="form-label">완료수량</label>
          <input
            id="completedQuantity"
            v-model.number="formData.completedQuantity"
            type="number"
            class="input"
            placeholder="0"
            min="0"
            :max="formData.targetQuantity"
          >
        </div>

        <div v-if="isEdit" class="form-group">
          <label for="status" class="form-label">상태</label>
          <select
            id="status"
            v-model="formData.status"
            class="select"
          >
            <option value="waiting">대기</option>
            <option value="in_progress">진행중</option>
            <option value="completed">완료</option>
            <option value="cancelled">취소</option>
          </select>
        </div>
      </div>

      <div class="form-section">
        <h4 class="section-title">필요 자재</h4>
        <div class="material-section">
          <div 
            v-for="(material, index) in formData.materials" 
            :key="index"
            class="material-row"
          >
            <div class="material-inputs">
              <input
                v-model="material.name"
                type="text"
                class="input"
                placeholder="자재명"
              >
              <input
                v-model.number="material.quantity"
                type="number"
                class="input"
                placeholder="수량"
                min="0"
              >
              <input
                v-model="material.unit"
                type="text"
                class="input"
                placeholder="단위"
              >
            </div>
            <button 
              type="button" 
              class="btn btn-sm btn-secondary"
              @click="removeMaterial(index)"
            >
              삭제
            </button>
          </div>
          <button 
            type="button" 
            class="btn btn-sm btn-secondary"
            @click="addMaterial"
          >
            + 자재 추가
          </button>
        </div>
      </div>

      <div class="form-group">
        <label for="priority" class="form-label">우선순위</label>
        <select
          id="priority"
          v-model="formData.priority"
          class="select"
        >
          <option value="normal">보통</option>
          <option value="high">높음</option>
          <option value="urgent">긴급</option>
        </select>
      </div>

      <div class="form-group">
        <label for="notes" class="form-label">특이사항</label>
        <textarea
          id="notes"
          v-model="formData.notes"
          class="textarea"
          rows="3"
          placeholder="작업 시 주의사항이나 특별 지시사항을 입력하세요"
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
import type { WorkOrder } from '@/types'

interface Props {
  modelValue: boolean
  workOrder?: WorkOrder | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', data: Partial<WorkOrder>): void
  (e: 'close'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const isEdit = computed(() => !!props.workOrder)

interface MaterialItem {
  name: string
  quantity: number
  unit: string
}

interface FormData {
  orderNumber: string
  productName: string
  targetQuantity: number
  completedQuantity: number
  equipment: string
  status: WorkOrder['status'] | ''
  startDate: string
  dueDate: string
  priority: string
  notes: string
  materials: MaterialItem[]
}

const initialFormData: FormData = {
  orderNumber: '',
  productName: '',
  targetQuantity: 0,
  completedQuantity: 0,
  equipment: '',
  status: '',
  startDate: '',
  dueDate: '',
  priority: 'normal',
  notes: '',
  materials: [{ name: '', quantity: 0, unit: '' }]
}

const formData = ref<FormData>({ ...initialFormData })

const isFormValid = computed(() => {
  return !!(
    formData.value.orderNumber &&
    formData.value.productName &&
    formData.value.targetQuantity > 0 &&
    formData.value.equipment &&
    formData.value.startDate &&
    formData.value.dueDate
  )
})

const resetForm = () => {
  formData.value = { 
    ...initialFormData,
    materials: [{ name: '', quantity: 0, unit: '' }]
  }
}

const loadWorkOrderData = () => {
  if (props.workOrder) {
    formData.value = {
      orderNumber: props.workOrder.orderNumber,
      productName: props.workOrder.productName,
      targetQuantity: props.workOrder.targetQuantity,
      completedQuantity: props.workOrder.completedQuantity,
      equipment: props.workOrder.equipment,
      status: props.workOrder.status,
      startDate: props.workOrder.startDate,
      dueDate: props.workOrder.dueDate,
      priority: 'normal',
      notes: '',
      materials: [{ name: '', quantity: 0, unit: '' }]
    }
  } else {
    resetForm()
    // 자동으로 작업지시번호 생성
    formData.value.orderNumber = `WO${Date.now().toString().slice(-8)}`
    
    // 기본 날짜 설정 (오늘부터 일주일 후)
    const today = new Date()
    const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000)
    
    formData.value.startDate = today.toISOString().split('T')[0]
    formData.value.dueDate = nextWeek.toISOString().split('T')[0]
  }
}

const addMaterial = () => {
  formData.value.materials.push({ name: '', quantity: 0, unit: '' })
}

const removeMaterial = (index: number) => {
  if (formData.value.materials.length > 1) {
    formData.value.materials.splice(index, 1)
  }
}

const handleSubmit = () => {
  if (!isFormValid.value) return

  const submitData: Partial<WorkOrder> = {
    orderNumber: formData.value.orderNumber,
    productName: formData.value.productName,
    targetQuantity: formData.value.targetQuantity,
    equipment: formData.value.equipment,
    startDate: formData.value.startDate,
    dueDate: formData.value.dueDate
  }

  if (isEdit.value && props.workOrder) {
    submitData.id = props.workOrder.id
    submitData.completedQuantity = formData.value.completedQuantity
    submitData.status = formData.value.status as WorkOrder['status']
  } else {
    submitData.id = Date.now().toString()
    submitData.completedQuantity = 0
    submitData.status = 'waiting'
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
    loadWorkOrderData()
  }
})

watch(() => props.workOrder, () => {
  if (props.modelValue) {
    loadWorkOrderData()
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

.form-section {
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-color);
}

.material-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.material-row {
  display: flex;
  gap: 0.5rem;
  align-items: end;
}

.material-inputs {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 0.5rem;
  flex: 1;
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
  
  .material-inputs {
    grid-template-columns: 1fr;
  }
  
  .material-row {
    flex-direction: column;
  }
}
</style>