<template>
  <div class="settings-page">
    <Card title="설정">
      <div class="settings-content">
        <div class="setting-section">
          <h3>시스템 설정</h3>
          <p>시스템 전반적인 설정을 관리합니다.</p>
        </div>
        
        <div class="setting-section">
          <h3>사용자 관리</h3>
          <p>사용자 권한과 계정을 관리합니다.</p>
        </div>
        
        <div class="setting-section">
          <h3>알림 설정</h3>
          <p>시스템 알림과 경고 설정을 관리합니다.</p>
        </div>
        
        <div class="setting-section">
          <h3>백업 및 복원</h3>
          <p>데이터 백업과 복원 기능을 제공합니다.</p>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import Card from '@/components/common/Card.vue'
</script>

<style scoped>
.settings-page {
  max-width: 800px;
}

.settings-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.setting-section {
  padding: 1.5rem;
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  background-color: var(--bg-secondary);
}

.setting-section h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
}

.setting-section p {
  margin: 0;
  color: var(--text-secondary);
}
</style>