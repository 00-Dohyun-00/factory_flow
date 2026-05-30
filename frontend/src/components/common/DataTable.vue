<template>
  <div class="table-container">
    <table class="table">
      <thead>
        <tr>
          <th v-for="column in columns" :key="column.key">
            {{ column.label }}
          </th>
          <th v-if="actions">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in data" :key="row.id">
          <td v-for="column in columns" :key="column.key">
            <slot :name="`cell-${column.key}`" :row="row" :value="row[column.key]">
              {{ row[column.key] }}
            </slot>
          </td>
          <td v-if="actions">
            <slot name="actions" :row="row"></slot>
          </td>
        </tr>
        <tr v-if="!data || data.length === 0">
          <td :colspan="columns.length + (actions ? 1 : 0)" style="text-align: center; color: var(--text-secondary); padding: 2rem;">
            데이터가 없습니다.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
interface Column {
  key: string
  label: string
}

interface Props {
  columns: Column[]
  data: any[]
  actions?: boolean
}

defineProps<Props>()
</script>